package com.serverside.api.resouces;

import com.serverside.api.domain.Group;
import com.serverside.api.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/group")
@CrossOrigin
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GroupResource {
    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<Group> save(@Valid @RequestBody Group group) {
        group = groupService.save(group);
        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Group>> findAll() {
        return ResponseEntity.ok(groupService.findAll());
    }
}
