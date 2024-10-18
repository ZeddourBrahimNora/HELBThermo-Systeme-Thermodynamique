# HELBThermo- Thermodynamic System
HELBThermo is an application developed in Java using JavaFX and integrating design patterns. This project aims to simulate a thermodynamic system where heat exchanges occur between cells. The application allows users to configure, customize, and observe these exchanges through an interactive interface.
# Features
* <b>Initial Configuration</b>: The user can define the system size, the number of heat sources, their initial temperature, and the arrangement of these sources (in an "X" or "+" configuration).
<div align="center">
  <img src="https://github.com/user-attachments/assets/56ab91d9-0a76-4bf3-96a9-4f5e957c1e3b" alt="Image du système de configuration" width="400">
</div>

* <b>Simulation Control</b>: "Play", "Pause", and "Reset" buttons to control the simulation.
* <b>Selection of different heating modes</b> (manual, total, first, even, odd).
* <b>Simulation speed adjustment</b> (normal, accelerated, slowed down).
 <div align="center"> <img src="https://github.com/user-attachments/assets/37280151-e890-4de2-80c2-ad6e82e927aa" alt="Image du système thermodynamique" width="400"> </div>

* <b>Dynamic Display</b>: Cells are displayed with colors ranging from blue (cold temperatures) to red (hot temperatures). The outside temperature and the average temperature of the active cells are also displayed.
* <b>Cell Management</b>: Ability to activate or deactivate heat sources, transform cells into dead cells, or set a new temperature for an existing heat source.
  <div align="center"> <img src="https://github.com/user-attachments/assets/613dfac5-d78e-4d11-8794-7a37ba1c3064" alt="Image de gestion des cellules" width="400"> </div>
* <b>Log File</b>: Upon closing the application, a log file is created with the state of the system at a given time, including information about the cells, their temperature, and their status.
* <b>Unit Testing</b>: Unit tests were performed using <b>JUnit5</b> to ensure the simulation functions properly in case of inconsistent values.
# Design Patterns used
* <b>MVC (Model-View-Controller)</b>: Clear separation between business logic (the model), user interface (the view), and user interactions (the controller).
* <b>Observer</b>: Allows communication between the model and the view, ensuring automatic data updates.
* <b>Factory</b>: Used to create different types of cells in the system, facilitating code management and modification.
* <b>Singleton</b>: Implemented for the logging system, ensuring that a single instance of the logger is used throughout the application.
