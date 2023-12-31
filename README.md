# NotesWebApplication
![Логотип](https://media.istockphoto.com/id/1250367378/de/vektor/post-it-notizen-haftende-note-schwarzes-symbol.jpg?s=612x612&w=0&k=20&c=WEnWaMxD50FGKd2M-qA6yxBIElMjuUQW5K8KP5RcaN8=)

Notes is a web application (backend part) built on REST principles. It provides registration/authentication based on tokens. Additionally, you have the ability to update your profile, delete it, or retrieve profile information. You can also create your own note, view a note by its identifier, retrieve all notes of a specific user, delete a note, and update a note.

# Endpoints:
- **POST: /authenticate** - Allows users to authenticate via email and password. Returns a token to be used for accessing other methods. Accessible to non-authenticated users.
- **POST: /register** - Allows users to register using their name, email, and password. After registration, users can authenticate and use other endpoints. Accessible to non-authenticated users.
- **GET: /users/{userId}** - Retrieves user information based on the identifier. You can only view your own information. Accessible to authenticated users.
- **DELETE: /users/{userId}** - Deletes a user based on the identifier. You can only delete your own user. Accessible to authenticated users.
- **PUT: /users/{userId}** - Updates a user based on the identifier. The endpoint expects a JSON body containing name, email, and password. You can only update your own user. After updating, you need to authenticate again and obtain a new token. Accessible to authenticated users.
- **POST: /notes?userId=?** - Creates a note based on the user identifier. The endpoint also expects a JSON body containing title and content. You can only create a note for yourself. Accessible to authenticated users.
- **GET: /notes/{noteId}?userId=?** - Reads a note based on the user identifier and note identifier. You can only view your own list of notes. Accessible to authenticated users.
- **GET: /notes/all?userId=?** - Reads all notes based on the user identifier. You can only read your own notes. Accessible to authenticated users.
- **DELETE: /notes/{noteId}?userId=?** - Deletes a note based on the user identifier and note identifier. You can only delete your own notes. Accessible to authenticated users.
- **PUT: /notes/{noteId}?userId=?** - Updates a note based on the user identifier and note identifier. The endpoint also expects a JSON body containing title and content. You can only update your own notes. Accessible to authenticated users.

# Structure:
- **Config package:**

| Class/Interface | Description                                                                                                                                                                                 |
|-----------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| SecurityConfig  | This class is used for configuring the security of a web application. It establishes rules for accessing different endpoints and configures the authentication and authorization mechanisms |

- **Controller package:**
 
| Controller               | Description                                                                                      |
|--------------------------|--------------------------------------------------------------------------------------------------|
| AuthenticationController | This controller has two endpoints related to user authentication and registration                |
| NoteController           | This controller contains five endpoints and is intended for working with notes (CRUD operations) |
| UserController           | This controller has three endpoints and is designed for working with users (CRUD operations)     |

- **DTO package:**

| DTO                      | Description                                                                                 |
|--------------------------|---------------------------------------------------------------------------------------------|
| AuthenticationRequestDto | This DTO is designed to receive user authentication data from the client                    |
| NoteRequestDto           | This DTO is designed to receive note data from the client                                   |
| NoteResponseDto          | This DTO is designed to provide a response from the server to the client regarding a note   |
| UserRequestDto           | This DTO is designed to receive data from the client regarding a user                       |
| UserResponseDto          | This DTO is designed to provide a response to the client from the server regarding a user.  |

- **Model package:**

| Model (entity) | Description                                                                                      |
|----------------|--------------------------------------------------------------------------------------------------|
| Note           | This model represents a note                                                                     |
| User           | This model represents a user                                                                     |
| UserDetails    | This model represents user data required for authentication and authorization in Spring Security |

- **Repository package:**

| Repository     | Description                                                       |
|----------------|-------------------------------------------------------------------|
| NoteRepository | This repository works with the database regarding the note model  |
| UserRepository | This repository works with the database regarding the user model  |

- **Security package:**
 
| Security              | Description                                                                                                                     |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------|
| JsonWebTokenFilter    | This class is used for working with JSON Web Tokens (JWT) in the context of authentication and authorization                    |
| JsonWebTokenUtil      | This class is a filter used to intercept and process HTTP requests in the context of authentication using JSON Web Tokens (JWT) |

- **Service package:**

| Service                | Description                                                                                 |
|------------------------|---------------------------------------------------------------------------------------------|
| NoteService            | This service is used for working with notes                                                 |
| UserDetailsService     | This service is used for authentication and authorization in the context of Spring Security |
| UserService            | This service is used for working with users                                                 |

- **Mapper package (service/mapper):**

| Mapper        | Description                                                                                      |
|---------------|--------------------------------------------------------------------------------------------------|
| NoteDtoMapper | This mapper is intended for transforming note objects between the model and DTO representations  |
| UserDtoMapper | This mapper is intended for transforming user objects between the model and DTO representations  |

- **Util package:**

| Class          | Description                                                         |
|----------------|---------------------------------------------------------------------|
| EmailValidator | This utility class is used for email validation based on a pattern  |

# Technologies used:

- Spring Boot Data Jpa 3.1.1
- Spring Boot Security 3.1.1
- Spring Boot Web 3.1.1
- Spring Boot Thymeleaf 3.1.1
- MySQL 8.0.33
- JSON Web Token 0.9.1
- JAXB API 2.4.0-b180830.0359
- Lombok 1.18.28

# How to run this project locally?
- make sure you have JDK 17 installed on your system;
- clone this repository;
- open this project (you can do this using Intellij IDEA or another development environment);
- please fill in the application.properties file and configure the database;
- run the Main method and enjoy this application;

# How to use this project?

- After configuring the database and running the project, open Postman (or download it if you don't have it);
- There are two available endpoints: /authentication/authenticate and /users/register. Start by registering a user and then authenticate yourself in the application;
- After authentication, you will receive a token. You need to use this token in the headers of Postman to access other endpoints (Authorization - header, and the value should be "Bearer" followed by a space, then paste the token you received after authentication);
- In other words, you should use the token in the headers when making a request to any endpoint to gain access to that endpoint.

# Additional information:

If you have any questions, please contact me via email at **alexstepanyak@gmail.com.**

**Author:** [**Oleksii Stepaniak**](https://github.com/oleksiistepaniak)