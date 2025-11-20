package com.example.springboot.backend.fullstack_backend.security;

import com.example.springboot.backend.fullstack_backend.entity.Usuario;
import com.example.springboot.backend.fullstack_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username es el correo
        Usuario usuario = usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        String role = usuario.getRol() != null ? usuario.getRol() : "CLIENTE";
        List<GrantedAuthority> auth = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
        return new User(usuario.getCorreo(), usuario.getContrasena() == null ? "" : usuario.getContrasena(), auth);
    }
}
