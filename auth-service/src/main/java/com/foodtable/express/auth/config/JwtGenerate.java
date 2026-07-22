package com.foodtable.express.auth.config;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.foodtable.express.auth.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;

/**
 * Componente responsável por gerar JSON Web Tokens (JWT) para usuários
 * autenticados.
 * Utiliza o algoritmo HMAC256 com uma chave secreta configurada nas
 * propriedades do projeto.
 */
@Service
public class JwtGenerate {

    /**
     * Chave secreta de assinatura do token. Injetada a partir do arquivo de
     * configuração (jwt.secret).
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Gera um token JWT assinado contendo o e-mail do usuário como assunto
     * (subject).
     *
     * @param user Dados do usuário autenticado.
     * @return String contendo o token JWT gerado e assinado.
     */
    public String generateToken(User user) {
        // Define o algoritmo de assinatura HMAC256 passando a chave secreta
        var algorithm = Algorithm.HMAC256(secret);

        // Constrói o token definindo emissor, assunto, expiração e assina com o
        // algoritmo
        return JWT.create()
                .withIssuer("foodtable-express") // Identifica o emissor do token
                .withSubject(user.getEmail()) // Identifica o assunto do token (o e-mail do usuário)
                .withExpiresAt(genExpirationDate()) // Define o tempo de expiração do token
                .sign(algorithm);
    }

    /**
     * Calcula a data/hora de expiração do token.
     *
     * @return Instant representando 2 horas no futuro no fuso horário do Brasil
     *         (-03:00).
     */
    private Instant genExpirationDate() {
        // Expira em 2 horas no fuso horário do Brasil (-03:00)
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

}
