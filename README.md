
# Loan Manager App

Loan Manager App es una aplicación diseñada para ayudar a las empresas de préstamos a gestionar sus operaciones diarias de manera eficiente. Con un panel de administrador y una interfaz de cajero, la aplicación permite la gestión de todos los préstamos, incluyendo el seguimiento de los días retrasados y los días restantes por pagar.

Además, la aplicación admite múltiples sucursales, lo que permite una gestión centralizada de todos los préstamos en diferentes locaciones. Los usuarios pueden actualizar los pagos diarios, semanales o personalizados y cerrar los préstamos de manera eficiente.

La aplicación también permite enviar recordatorios personalizados a través de WhatsApp, lo que facilita la comunicación con los clientes y mejora la eficiencia del proceso de cobro. La administración de caja es una de las características clave de la aplicación, permitiendo un seguimiento detallado de los ingresos y gastos.

Además, los usuarios pueden generar informes de caja personalizados por fechas, lo que les permite conocer el estado financiero de la empresa en cualquier momento. La aplicación también permite registrar préstamos personalizados con intereses y plazos ajustables, y realizar un redondeo de préstamos para una gestión más precisa y eficiente.

En resumen, Loan Manager App es una herramienta útil para cualquier empresa de préstamos que busque optimizar sus operaciones y mejorar la eficiencia de su proceso de cobro.


## Features

- Menus (Dashboard / Finanzas / Registro)
- Géstion de todos los préstamos (días retrasados - días faltantes por pagar)
- Múltiples Sucursales
- Admin Panel / Cashier
- Actualiza pagos diarios / semanales / personalizados
- Cierra préstamos 
- Envía recordatorios personalizados mediante Whatsapp
- Administración de caja
- Reporte de caja por fechas personalizados
- Registro de préstamos personalizados (ínteres ó plazos )
- Redondeo de préstamos
- Admob


## Screenshots

![Cover](https://user-images.githubusercontent.com/60039961/218292137-abf70b8b-3a3c-4e3c-b609-c6aa3f4671a9.jpg)


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
- Username: superadmin@gmail.com
- Password: 123456

Admin credentials
- Username: admin@gmail.com
- Password: 123456

Cashier credentials
- Username: 1@gmail.com
- Password: 123456

## Authors

- [@ahuamana](https://www.github.com/ahuamana)

