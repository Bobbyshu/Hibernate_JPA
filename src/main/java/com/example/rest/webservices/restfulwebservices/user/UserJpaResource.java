package com.example.rest.webservices.restfulwebservices.user;

import com.example.rest.webservices.restfulwebservices.jpa.UserRepository;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserJpaResource {
  private UserDaoService service;

  private UserRepository repository;

  public UserJpaResource(UserDaoService service, UserRepository repository) {
    this.service = service;
    this.repository = repository;
  }

  @GetMapping("/jpa/users")
  public List<User> retrieveAllUsers() {
    return repository.findAll();
  }

  @GetMapping("/jpa/users/{id}")
  public EntityModel<User> retrieveUser(@PathVariable int id) {
    User user = service.findOne(id);

    if (user == null) {
      throw new UserNotFoundException("id" + id);
    }

    EntityModel<User> entityModel = EntityModel.of(user);
    WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retrieveAllUsers());
    entityModel.add(link.withRel("all-users"));

    return entityModel;
  }

  @DeleteMapping("/jpa/users/{id}")
  public EntityModel<User> deleteUser(@PathVariable int id) {
    service.deleteById(id);
  }

  @PostMapping("/jpa/users")
  public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
    User savedUser = service.save(user);

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}").buildAndExpand(savedUser.getId()).toUri();

    return ResponseEntity.created(location).build();
  }
}