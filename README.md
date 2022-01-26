# Tutorial
---

1. [Agregar Recordatorio](web/addReminders.md)
2. [Buscar](web/find.md)
3. [Opciones](web/settings.md)
4. [Archivados](web/archived.md)
5. [Permisos](web/permissions.md)

# Notas "Tecnicas"
---
### Permisos y ¿Por que los necesitamos?

	Al instalar o utilizar esta aplicación se le pediran diversos permisos para su funcionamiento.
	Estos son el uso de la camara, el microfono y el almacenamiento del dispocitivo. Todos son utilizados para crear y guardar los diversos tipos de recordatorios.
	Puede negarlos tranquilamente si es que usted no decea que tengamos acceso a dichas características, pero no podra utilizar dicha funcion.

### Bugs Conocidos

- ~~Al ingresar las fechas: Si se cambia mucho la hora suma dias a la fecha.~~

- No agrega la foto a los albunes
- Cartel de error al cargar aparece siempre que la lista esta vaci. 
- ~~El menu esta visible en todas las pantallas y explota al usarlo desde un lugar que no sea el main.~~
- Imagen del Boton de enviar en los Audios en la pantalla de creacion no esta sentrado
- SeekBar esta media bugeada con el por el handle, por que es cada medio seg si lo muevo o lo pone medio seg antes o despues
- ~~Aveces no carga la lista re recordatorios, necesita reiniciarce (Todavia no encontre la causa)~~
- Si no se coloca titulo en el audio se le asigna el nombre del archivo, pero el nombre es muy grand para el holder
### Third party
---
#### Libraries
- `implementation 'com.chibde:audiovisualizer:2.2.0'` ->[Link](https://github.com/GautamChibde/android-audio-visualizer "Link")
- `implementation 'com.github.3llomi:RecordView:3.0.2'` ->[Link](https://github.com/3llomi/RecordView "Link")
- `implementation "com.leinardi.android:speed-dial:3.2.0"` -> [Link](https://github.com/leinardi/FloatingActionButtonSpeedDial "Link")
#### Imagenes
Libres de derechos de autor
https://pixabay.com/
