> [!IMPORTANT]  
> ## Project status / Estado del proyecto  
> 
> **EN:** This Android app is now in **maintenance mode**.  
> We are currently working on the **next generation of Loan Manager**, built with **Kotlin Multiplatform (KMP)**, so it can run on **Android and iOS**.  
> This repository will continue to receive **bug fixes and security updates**, but **no major new features** are planned here.  
> 
> **ES:** Esta aplicación Android ahora se encuentra en **modo mantenimiento**.  
> Actualmente estamos trabajando en la **siguiente generación de Loan Manager**, desarrollada con **Kotlin Multiplatform (KMP)** para que funcione tanto en **Android como en iOS**.  
> Este repositorio seguirá recibiendo **correcciones de errores y actualizaciones de seguridad**, pero **no se añadirán nuevas funcionalidades grandes** aquí.  
> 
> Más adelante compartiremos novedades sobre el nuevo proyecto multiplataforma.


# Loan Manager App

![Prestamist - app - background v4 0 0](https://github.com/user-attachments/assets/0d5a0ac6-ab06-449f-8e48-99d22604749d)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.8.0-blue.svg)](https://kotlinlang.org)
[![Android Studio](https://img.shields.io/badge/Android%20Studio-2023.1.1-green.svg)](https://developer.android.com/studio)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

Loan Manager App es una aplicación diseñada para ayudar a las empresas de préstamos a gestionar sus operaciones diarias de manera eficiente. Con un panel de administrador y una interfaz de cajero, la aplicación permite la gestión de todos los préstamos, incluyendo el seguimiento de los días retrasados y los días restantes por pagar.

Además, la aplicación admite múltiples sucursales, lo que permite una gestión centralizada de todos los préstamos en diferentes locaciones. Los usuarios pueden actualizar los pagos diarios, semanales o personalizados y cerrar los préstamos de manera eficiente.

La aplicación también permite enviar recordatorios personalizados a través de WhatsApp, lo que facilita la comunicación con los clientes y mejora la eficiencia del proceso de cobro. La administración de caja es una de las características clave de la aplicación, permitiendo un seguimiento detallado de los ingresos y gastos.

Además, los usuarios pueden generar informes de caja personalizados por fechas, lo que les permite conocer el estado financiero de la empresa en cualquier momento. La aplicación también permite registrar préstamos personalizados con intereses y plazos ajustables, y realizar un redondeo de préstamos para una gestión más precisa y eficiente.

En resumen, Loan Manager App es una herramienta útil para cualquier empresa de préstamos que busque optimizar sus operaciones y mejorar la eficiencia de su proceso de cobro.


## 🚀 Features

- Menus (Dashboard / Finanzas / Registro, Perfil, Registro)
- Géstion de todos los préstamos (días retrasados - días faltantes por pagar)
- Múltiples Sucursales
- Admin Panel / Cashier
- Actualiza pagos diarios / semanales / personalizados
- Cierra préstamos 
- Envía recordatorios personalizados mediante Whatsapp & Sms
- Administración de caja
- Prestamos personalizados (DIARIO, SEMANAL, QUINCENAL, MENSUAL, BIMESTRAL, TRIMESTRAL, SEMESTRAL, ANUAL)
- Reporte de caja por fechas personalizados
- Registro de préstamos personalizados (ínteres ó plazos )
- Genera voucher de pago
- Opciones para compartir voucher por dispositivo
- Redondeo de préstamos
- Modulo de cliente (v5.0.0)
- Automatización de envio de bouchers por email con RESEND.com (Opcional - V4.1.0)

## 🛠 Tech Stack & Open-source libraries

- Minimum SDK level 24
- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous programming
- [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) - A cold asynchronous data stream
- [JetPack](https://developer.android.com/jetpack)
    - Lifecycle - Observe Android lifecycles and handle UI states
    - ViewModel - UI related data holder, lifecycle aware
    - Navigation Component - Handle everything needed for in-app navigation
- [Koin](https://insert-koin.io/) - Dependency Injection framework
- [Firebase](https://firebase.google.com/) - Backend platform for authentication and realtime database
- [Material-Components](https://github.com/material-components/material-components-android) - Material design components

## 🏗️ Architecture
- Clean Architecture
- MVI Architecture (View - View - Intent)
- Repository pattern
- SOLID Principles
- Jetpack Libraries

  
## Installation

0. Configuracion del proyecto en Firebase
   - Crear un proyecto en firebase
   - Configurar Firestore Database
   - Configurar RealTime Database
   - Configurar Authentication method using Email/Password

1. Configurar Firestore Database
    - Configurar rules
    ```bash
    rules_version = '2';
    service cloud.firestore {
        match /databases/{database}/documents {
            match /{document=**} {
                allow read, write:if(request.auth) != null
                }
            }
        }
    ```
    - Configurar Indices para el modulo de finanzas
    <img width="1565" alt="Setup finance module" src="https://github.com/user-attachments/assets/4878c641-2033-423b-9433-687e04519914">

1.1 Configurar Realtime Database

```bash
      {
      "rules": {
        ".read": true,
        ".write": false
          }
      }
```

2. Reemplazar el archivo Google Services de Firebase
```bash
  Cambiar el archivo google-services.json que se encuentra dentro de la carpeta 
  ./prestamist/app/google-services.json
```
3. Cargar las sucursales actuales dentro de Realtime Database

```bash
  Ejemplo:

  {
  "sucusales": {
    "sucursal1": {
      "address": "Av lima 1456",
      "id": 0,
      "name": "Pichanaki A"
    },
    "sucursal2": {
      "address": "Jr. Mediaterraneo 1587",
      "id": 16,
      "name": "Pichanaki B"
    },
    "sucursal3": {
      "address": "Av. circunvalacion s/n",
      "id": 18,
      "name": "Puerto Bermudez"
    }
  }
}
```
4. Configuración de Notificaciones Automáticas (OPCIONAL)

    Para configurar notificaciones automáticas utilizando la API de Resend, sigue estos pasos:

    1. Crear una cuenta en Resend
    2. Configurar un nuevo proyecto en Resend
    3. Configurar tu dominio en Resend
    3. Obtener el API Key de Resend
    4. Configurar el API Key en el local.properties del proyecto

    ```bash
    RESEND_API_KEY=<REPLACE_WITH_API_KEY>
    ```
    
    Para configurar el recipient from en el envio de mensajes, debes configurar el archivo PADATAConstants.kt

    ```bash
    const val RECIPIENT_FROM = "PrestamistApp <info@paparazziteam.work>"
    ```

4. Sincronizar el proyecto y vincularlo con Firebase

```bash
  Disfrútalo 💕 💕 💕
```
## Demo

Super Admin credentials
- Username: superadmin@gmail.com
- Password: 123456

Admin credentials
- Username: admin@gmail.com
- Password: 123456

Cashier credentials
- Username: cashier@gmail.com
- Password: 123456

## Authors

- [@ahuamana](https://www.github.com/ahuamana)

## Licence
Copyright 2024 Antony Huaman

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

