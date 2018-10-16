package me.exrates.controller;

import me.exrates.model.main.Notification;
import me.exrates.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController/*("/notifications")*/
public class NotificationControllerRest {

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "/notifications/findAll", method = GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Notification> findNotificationsByUser(Principal principal) {
        return notificationService.findAllByUser(principal.getName());
    }

    @RequestMapping(value = "/notifications/markRead", method = POST)
    public ResponseEntity<Void> markRead(@RequestParam Long notificationId) {
        notificationService.setRead(notificationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/notifications/remove", method = POST)
    public ResponseEntity<Void> remove(@RequestParam Long notificationId) {
        notificationService.remove(notificationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/notifications/markReadAll", method = POST)
    public ResponseEntity<Void> markReadAll(Principal principal) {
        notificationService.setReadAllByUser(principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/notifications/removeAll", method = POST)
    public ResponseEntity<Void> removeAll(Principal principal) {
        notificationService.removeAllByUser(principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
