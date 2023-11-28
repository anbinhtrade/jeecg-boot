package vn.abs.erp.notification.controller;


import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.abs.erp.external.ExtUserClient;
import vn.abs.erp.dto.UserSearchDto;

import java.io.IOException;
import java.util.List;

@Slf4j
@Api(tags="User Service")
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private ExtUserClient extUserClient;


    @GetMapping("/search")
    public ResponseEntity<List<UserSearchDto>> searchUser(@RequestParam String searchTerm) throws IOException {
        return extUserClient.extSearch(searchTerm);
    }
}
