package com.trullo.services;

import com.trullo.interfaces.dao.ReminderTargetDAOInterface;
import com.trullo.interfaces.services.*;
import com.trullo.interfaces.notification.*;
import com.trullo.notification.*;
import com.trullo.model.*;
import com.trullo.exception.NotificationException;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class RemoteReminderTest {
    static class Remote implements ScheduledNotifier {
        int calls; boolean fail; LocalDateTime scheduled; String title;
        public String channel(){return "whatsapp";}
        public void validate(String address){}
        public void schedule(String address,String title,LocalDateTime scheduled){
            calls++;this.title=title;this.scheduled=scheduled;
            if(fail)throw new NotificationException("API no disponible");
        }
    }
    static class Email implements Notifier {
        int calls;
        public String channel(){return "email";}
        public void validate(String address){}
        public void send(String address,Message message){calls++;}
    }
    @SuppressWarnings("unchecked")
    static <T>T returning(Class<T> type,Object value){return (T)Proxy.newProxyInstance(type.getClassLoader(),new Class[]{type},(p,m,a)->value);}
    private final List<ReminderTarget> targets=new ArrayList<>();
    private final Reminder reminder=new Reminder(1L,2L,LocalDateTime.now().plusDays(1));
    private final Task task=new Task(2L,"Enviar informe","Descripcion",TaskStatus.PENDIENTE,TaskPriority.MEDIA,null,null);
    private final ReminderService reminders=returning(ReminderService.class,reminder);
    private final TaskService tasks=returning(TaskService.class,task);
    private ReminderTargetDAOInterface dao(){
        return (ReminderTargetDAOInterface)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{ReminderTargetDAOInterface.class},(p,m,a)->{
            switch(m.getName()){
                case "create": { var t=(ReminderTarget)a[0]; t.setId((long)targets.size()+1); targets.add(t);return t; }
                case "delete": targets.removeIf(t->t.getId().equals(a[0]));return null;
                case "findPending": case "findByReminderId": return List.copyOf(targets);
                case "markSent": targets.stream().filter(t->t.getId().equals(a[0])).forEach(t->t.setSentAt(LocalDateTime.now()));return null;
                default: return null;
            }
        });
    }
    @Test public void schedulesAtCreationAndNeverRequeuesAtDueTime(){
        var remote=new Remote();var email=new Email();var registry=new NotifierRegistry(List.of(remote,email));
        var service=new ReminderTargetServiceImpl(dao(),reminders,tasks,registry);
        var target=service.agregarDestino(1L,"whatsapp","1234567");
        service.agregarDestino(1L,"email","test@example.com");
        assertEquals(1,remote.calls);assertEquals("Enviar informe",remote.title);assertEquals(reminder.getScheduledAt(),remote.scheduled);
        var scheduler=new ReminderScheduler(service,new ReminderMessageBuilder(reminders,tasks),registry);
        assertEquals(1,scheduler.sendPending());assertEquals(1,email.calls);assertEquals(1,remote.calls);
        assertNull(target.getSentAt()); // Programado no significa entregado.
    }
    @Test public void failedSubmissionCleansUpLocalTarget(){
        var remote=new Remote();remote.fail=true;
        var service=new ReminderTargetServiceImpl(dao(),reminders,tasks,new NotifierRegistry(List.of(remote)));
        assertThrows(NotificationException.class,()->service.agregarDestino(1L,"whatsapp","1234567"));
        assertTrue(targets.isEmpty());assertEquals(1,remote.calls);
    }
}
