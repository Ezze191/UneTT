 Unett - Red Social 
Unett es un proyecto escolar que consiste en una red social desarrollada como página web y aplicación móvil.

Tecnologías utilizadas:
Frontend Web: HTML y JavaScript puro (sin frameworks).

Backend: MySQL y APIs REST desarrolladas en PHP.

Servicios en la nube:

AWS: Hospedaje y despliegue de la página web.

Firebase: Autenticación de usuarios y almacenamiento de archivos (fotos, videos).

Aplicación móvil: Desarrollada en Android Studio utilizando Kotlin, conectada a los mismos servicios mediante las APIs REST.

 Pantalla de Inicio de Sesión
<img width="798" height="450" alt="home" src="https://github.com/user-attachments/assets/dfc5d468-5951-417c-aaf7-4d0e012a4cb5" />
Desde esta pantalla, los usuarios pueden iniciar sesión ingresando su correo y contraseña.
Si las credenciales son correctas, se habilitarán las funciones de la red social.

Además, en la parte inferior de la página (utilizando la barra de desplazamiento) hay un botón para descargar la app móvil si el usuario accede desde su celular.

 Sección "Acerca de"
<img width="804" height="286" alt="acerca de" src="https://github.com/user-attachments/assets/91f55283-d5a7-4796-a41c-96923cb9d6b1" />
Vista informativa donde se presenta información general sobre la red social Unett.

 Registro de Cuenta Nueva
<img width="887" height="539" alt="register" src="https://github.com/user-attachments/assets/1d3c096e-942c-4f41-bbdf-3dee0adf2730" />
Esta ventana se accede desde el login principal mediante la opción "Crear cuenta nueva".

Los usuarios deben ingresar:

Nombre de usuario

Correo institucional de la UTT

Contraseña

Una vez registrado, el sistema envía un correo de verificación para activar la cuenta y asignarle permisos de acceso.

 Verificación de Cuenta
<img width="418" height="494" alt="verificacion" src="https://github.com/user-attachments/assets/bfb1fcc7-9f4d-4e07-8c7b-5a2519822549" />
Firebase envía un correo electrónico de autenticación.
El usuario debe hacer clic en el enlace de verificación para activar su cuenta y acceder a los servicios de Unett.

 Crear Publicación
<img width="496" height="268" alt="crear_publicacion" src="https://github.com/user-attachments/assets/9f75d3da-1950-4d1d-870f-406574e66824" />
Pantalla diseñada para subir y publicar contenido como fotos o videos.
También permite:

Comentar publicaciones.

Dar "like".

Eliminar tus propias publicaciones.

 Ver y Editar Perfil
<img width="539" height="246" alt="ver_perfil" src="https://github.com/user-attachments/assets/b41c2f30-1e87-4fd5-b67e-525e8a593045" />
Desde esta sección, los usuarios pueden:

Editar su nombre de usuario.

Cambiar su contraseña.

Publicar su autobiografía.

Incluye:

Botón para cerrar sesión.

Formulario de validación mediante contraseña para confirmar cambios.

 Funciones Principales
Registro e inicio de sesión con verificación por correo electrónico.

Subida y publicación de fotos y videos.

Interacción social (comentarios, likes, eliminación de publicaciones).

Edición de perfil y contraseña.

Aplicación web y móvil sincronizadas mediante APIs REST.
