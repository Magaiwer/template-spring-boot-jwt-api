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
import java.util.Optional;

@RestController
@RequestMapping(value = "/group")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GroupResource {
    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<Group> save(@Valid @RequestBody Group group) {
        group = groupService.save(group);
        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Group> update(@Valid @RequestBody Group group) {
        group = groupService.save(group);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Group> delete(@PathVariable Long id) {
        groupService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Group>> findAll() {
        return ResponseEntity.ok(groupService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> findById(@PathVariable Long id) {
        Optional<Group> group = groupService.findById(id);
        return group.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}