package com.santander.bank.Controller.User;

import com.santander.bank.DTO.User.FindCpfDTO;
import com.santander.bank.DTO.User.UserDTO;
import com.santander.bank.Models.Users.User;
import com.santander.bank.Repository.User.UserRepo;
import com.santander.bank.Services.Token.TokenService;
import com.santander.bank.Services.User.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;

    @GetMapping(value = "")
    public ResponseEntity<List<User>> getAll() {
        List<User> uL = userRepo.findAll();
        if (uL.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(uL);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<User> get(@PathVariable String id) {
        if (userRepo.findById(id).isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(userRepo.findById(id).get());
    }

    @GetMapping(value = "/cpf")
    public ResponseEntity<User> findByCpf(@RequestBody @Valid FindCpfDTO data, @RequestHeader Map<String, String> headers) {
        User u = userRepo.findByUserCpf(data.cpf());
        String token = headers.get("authorization").replace("Bearer ", "");
        String cpf = tokenService.validate(token);

        if (!cpf.equals(data.cpf())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        if (u == null) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        return ResponseEntity.ok(u);
    }

    @PostMapping(value = "/new")
    public ResponseEntity<String> insert(@RequestBody UserDTO user) {
        userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        userRepo.deleteById(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}



