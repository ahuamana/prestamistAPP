
# Loan Manager App

![Prestamist - app - background v3 0 0](https://github.com/user-attachments/assets/d9f074f9-b67d-4d74-a706-9a73b393c846)

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

- Menus (Dashboard / Finanzas / Registro)
- Géstion de todos los préstamos (días retrasados - días faltantes por pagar)
- Múltiples Sucursales
- Admin Panel / Cashier
- Actualiza pagos diarios / semanales / personalizados
- Cierra préstamos 
- Envía recordatorios personalizados mediante Whatsapp
- Administración de caja
- Prestamos personalizados (DIARIO, SEMANAL, QUINCENAL, MENSUAL, BIMESTRAL, TRIMESTRAL, SEMESTRAL, ANUAL)
- Reporte de caja por fechas personalizados
- Registro de préstamos personalizados (ínteres ó plazos )
- Redondeo de préstamos
- Admob

## 🛠 Tech Stack & Open-source libraries

- Minimum SDK level 24
- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous programming
- [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) - A cold asynchronous data stream
- [JetPack](https://developer.android.com/jetpack)
    - Lifecycle - Observe Android lifecycles and handle UI states
    - ViewModel - UI related data holder, lifecycle aware
    - Room - Construct database
    - Navigation Component - Handle everything needed for in-app navigation
- [Dagger Hilt](https://dagger.dev/hilt/) - Dependency Injection framework
- [Firebase](https://firebase.google.com/) - Backend platform for authentication and realtime database
- [Material-Components](https://github.com/material-components/material-components-android) - Material design components

## 🏗️ Architecture
- Clean Architecture
- MVVM Architecture (View - ViewModel - Model)
- Repository pattern
- SOLID Principles
- 
## Installation

1. Reemplazar el archivo Google Services de Firebase
```bash
  Cambiar el archivo google-services.json que se encuentra dentro de la carpeta 
  ./prestamist/app/google-services.json
```
2. Cargar las sucursales actuales dentro de Realtime Database

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

3. Sincronizar el proyecto y vincularlo con Firebase

```bash
  Disfrútalo 💕 💕 💕
```
## Demo

Super Admin credentials
- Username: demo@gmail.com
- Password: 123456

Admin credentials
- Username: admin@gmail.com
- Password: 123456

Cashier credentials
- Username: 1@gmail.com
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

