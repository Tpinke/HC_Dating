package com.tanhua.sso.controller;

import com.tanhua.sso.service.HuanXinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user/huanxin")
public class HuanXinController {

    @Autowired
    private HuanXinService huanXinService;

    /**
     * 添加联系人
     *
     * @param userId
     * @param friendId
     * @return
     */
    @PostMapping("contacts/{owner_username}/{friend_username}")
    public ResponseEntity<Void> contactUsers(@PathVariable("owner_username") Long userId,
                                             @PathVariable("friend_username") Long friendId) {
        try {
            boolean result = this.huanXinService.contactUsers(userId, friendId);
            if (result) {
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 删除联系人
     *
     * @param userId
     * @param friendId
     * @return
     */
    @PostMapping("remove/{owner_username}/{friend_username}")
    public ResponseEntity<Void> removeUsers(@PathVariable("owner_username") Long userId,
                                             @PathVariable("friend_username") Long friendId) {
        try {
            boolean result = this.huanXinService.removeUsers(userId, friendId);
            if (result) {
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 发送系统消息
     *
     * @param target
     * @param msg
     * @param type
     * @return
     */
    @PostMapping("messages")
    public ResponseEntity<Void> sendMsg(@RequestParam("target") String target,
                                        @RequestParam("msg") String msg,
                                        @RequestParam(value = "type", defaultValue = "txt") String type) {
        try {
            boolean result = this.huanXinService.sendMsg(target, type, msg);
            if (result) {
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
