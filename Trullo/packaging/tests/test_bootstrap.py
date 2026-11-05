import importlib.util
import json
import os
from pathlib import Path
import shutil
import socket
import subprocess
import sys
import tempfile
import unittest
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[2]
spec = importlib.util.spec_from_file_location("bootstrap", ROOT / "packaging/bootstrap.py")
setup = importlib.util.module_from_spec(spec)
spec.loader.exec_module(setup)


class SetupTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix="trullo-setup-test-")
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)

    def test_state_and_passwords_are_preserved(self):
        first = setup.database_state(self.root, 55555)
        self.assertEqual(first, setup.database_state(self.root, 55556))
        self.assertEqual(64, len(first["app_password"]))
        self.assertNotEqual(first["admin_password"], first["app_password"])
        if sys.platform != "win32":
            self.assertEqual(0o600, (self.root / "database-state.json").stat().st_mode & 0o777)

    def test_unknown_cluster_is_never_reinitialized(self):
        (self.root / "postgres").mkdir()
        (self.root / "postgres/PG_VERSION").write_text("17")
        with self.assertRaisesRegex(RuntimeError, "sin su configuracion"):
            setup.database_state(self.root, 54329)

    def test_config_preserves_credentials_and_smtp(self):
        config = self.root / "config"
        state = setup.database_state(self.root, 54329)
        setup.write_config(config, self.root / "first", self.root, state)
        original = (config / ".env").read_text()
        with (config / ".env").open("a") as output:
            output.write('SMTP_FROM="cuenta@example.com"\n')
        setup.write_config(config, self.root / "second", self.root, state)
        updated = (config / ".env").read_text()
        self.assertIn('DB_PASSWORD="' + state["app_password"] + '"', updated)
        self.assertIn('SMTP_FROM="cuenta@example.com"', updated)
        self.assertIn('/second/tools/uv', updated)
        self.assertEqual(1, updated.count('UV_EXECUTABLE='))
        self.assertIn('DB_PORT=54329', original)

    @unittest.skipIf(sys.platform == "win32", "Bloqueo probado en Linux")
    def test_second_instance_is_rejected(self):
        with setup.setup_lock(self.root):
            with self.assertRaises(RuntimeError):
                with setup.setup_lock(self.root):
                    self.fail("Segundo lock aceptado")
        with setup.setup_lock(self.root):
            pass

    def test_missing_postgres_fails_before_modifying_database(self):
        with self.assertRaisesRegex(RuntimeError, "binarios"):
            setup.pg_bin(self.root, self.root)

    def test_platform_paths(self):
        with patch.dict(os.environ, {"APPDATA": str(self.root / "roaming"), "LOCALAPPDATA": str(self.root / "local")}), patch.object(sys, "platform", "win32"):
            self.assertEqual((self.root / "roaming/Trullo", self.root / "local/Trullo"), setup.user_paths())
        with patch.dict(os.environ, {"XDG_CONFIG_HOME": str(self.root / "config"), "XDG_DATA_HOME": str(self.root / "data")}), patch.object(sys, "platform", "linux"):
            self.assertEqual((self.root / "config/trullo", self.root / "data/trullo"), setup.user_paths())


@unittest.skipUnless(os.getenv("TRULLO_SETUP_TEST_POSTGRES") == "1", "Opt-in: cluster temporal aislado")
class PostgresSetupTest(unittest.TestCase):
    def test_new_installation_upgrade_and_unknown_schema(self):
        with tempfile.TemporaryDirectory(prefix="trullo-pg-test-") as name:
            root = Path(name)
            data = root / "data"
            data.mkdir()
            installation = root / "application"
            shutil.copytree(ROOT / "src/main/resources/sql", installation / "setup/sql")
            binaries = setup.pg_bin(installation)
            with socket.socket() as sock:
                sock.bind(("127.0.0.1", 0))
                port = sock.getsockname()[1]
            try:
                first = setup.prepare_database(installation, data, binaries, port)
                env = os.environ.copy()
                env.update(PGHOST="127.0.0.1", PGPORT=str(port), PGUSER="trullo_app", PGPASSWORD=first["app_password"])
                def sql(text):
                    return subprocess.check_output([str(binaries["psql"]), "-XAt", "-v", "ON_ERROR_STOP=1", "-d", "trullo", "-c", text], env=env, text=True).strip()
                self.assertEqual("0", sql("SELECT count(*) FROM tasks"))
                sql("INSERT INTO notes(title,content) VALUES ('conservar','actualizacion')")
                second = setup.prepare_database(installation, data, binaries, port)
                self.assertEqual(first, second)
                self.assertEqual("conservar", sql("SELECT title FROM notes"))
                sql("DROP TABLE trullo_schema_version")
                with self.assertRaisesRegex(RuntimeError, "tablas ajenas"):
                    setup.prepare_database(installation, data, binaries, port)
                self.assertEqual("conservar", sql("SELECT title FROM notes"))
            finally:
                subprocess.run([str(binaries["pg_ctl"]), "-D", str(data / "postgres"), "-m", "fast", "-w", "stop"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)


if __name__ == "__main__":
    unittest.main()
