USE mechanic_workshop;

INSERT INTO user_type (name, description) VALUES
('Administrador', 'Administrador del sistema con acceso total'),
('Empleado', 'Empleado del taller (mecánico, asistente)'),
('Especialista', 'Especialista técnico (electricista, diagnósticos)'),
('Cliente', 'Propietario de vehículo que solicita servicios'),
('Proveedor', 'Proveedor de repuestos y servicios');

INSERT INTO gender (name, description) VALUES
('Masculino', 'Género masculino'),
('Femenino', 'Género femenino'),
('Otro', 'Otro género');

INSERT INTO department (name) VALUES
('Guatemala'),
('El Progreso'),
('Sacatepéquez'),
('Chimaltenango'),
('Escuintla'),
('Santa Rosa'),
('Sololá'),
('Totonicapán'),
('Quetzaltenango'),
('Suchitepéquez'),
('Retalhuleu'),
('San Marcos'),
('Huehuetenango'),
('Quiché'),
('Baja Verapaz'),
('Alta Verapaz'),
('Petén'),
('Izabal'),
('Zacapa'),
('Chiquimula'),
('Jalapa'),
('Jutiapa');

INSERT INTO municipality (name, department_id) VALUES
('Guatemala', 1),
('Santa Catarina Pinula', 1),
('San José Pinula', 1),
('San José del Golfo', 1),
('Palencia', 1),
('Chinautla', 1),
('San Pedro Ayampuc', 1),
('Mixco', 1),
('San Pedro Sacatepéquez', 1),
('San Juan Sacatepéquez', 1),
('San Raymundo', 1),
('Chuarrancho', 1),
('Fraijanes', 1),
('Amatitlán', 1),
('Villa Nueva', 1),
('Villa Canales', 1),
('Petapa', 1);

INSERT INTO municipality (name, department_id) VALUES
('Guastatoya', 2),
('Morazán', 2),
('San Agustín Acasaguastlán', 2),
('San Cristóbal Acasaguastlán', 2),
('El Jícaro', 2),
('Sansare', 2),
('Sanarate', 2),
('San Antonio La Paz', 2);

INSERT INTO municipality (name, department_id) VALUES
('Antigua Guatemala', 3),
('Jocotenango', 3),
('Pastores', 3),
('Sumpango', 3),
('Santo Domingo Xenacoj', 3),
('Santiago Sacatepéquez', 3),
('San Bartolomé Milpas Altas', 3),
('San Lucas Sacatepéquez', 3),
('Santa Lucía Milpas Altas', 3),
('Magdalena Milpas Altas', 3),
('Santa María de Jesús', 3),
('Ciudad Vieja', 3),
('San Miguel Dueñas', 3),
('Alotenango', 3),
('San Antonio Aguas Calientes', 3),
('Santa Catarina Barahona', 3);

INSERT INTO municipality (name, department_id) VALUES
('Chimaltenango', 4),
('San José Poaquil', 4),
('San Martín Jilotepeque', 4),
('San Juan Comalapa', 4),
('Santa Apolonia', 4),
('Tecpán Guatemala', 4),
('Patzún', 4),
('Pochuta', 4),
('Patzicía', 4),
('Santa Cruz Balanyá', 4),
('Acatenango', 4),
('Yepocapa', 4),
('San Andrés Itzapa', 4),
('Parramos', 4),
('Zaragoza', 4),
('El Tejar', 4);

INSERT INTO municipality (name, department_id) VALUES
('Escuintla', 5),
('Santa Lucía Cotzumalguapa', 5),
('La Democracia', 5),
('Siquinalá', 5),
('Masagua', 5),
('Tiquisate', 5),
('La Gomera', 5),
('Guanagazapa', 5),
('San José', 5),
('Iztapa', 5),
('Palín', 5),
('San Vicente Pacaya', 5),
('Nueva Concepción', 5);

INSERT INTO municipality (name, department_id) VALUES
('Cuilapa', 6),
('Barberena', 6),
('Santa Rosa de Lima', 6),
('Casillas', 6),
('San Rafael Las Flores', 6),
('Oratorio', 6),
('San Juan Tecuaco', 6),
('Chiquimulilla', 6),
('Taxisco', 6),
('Santa María Ixhuatán', 6),
('Guazacapán', 6),
('Santa Cruz Naranjo', 6),
('Pueblo Nuevo Viñas', 6),
('Nueva Santa Rosa', 6);

INSERT INTO municipality (name, department_id) VALUES
('Sololá', 7),
('San José Chacayá', 7),
('Santa María Visitación', 7),
('Santa Lucía Utatlán', 7),
('Nahualá', 7),
('Santa Catarina Ixtahuacán', 7),
('Santa Clara La Laguna', 7),
('Concepción', 7),
('San Andrés Semetabaj', 7),
('Panajachel', 7),
('Santa Catarina Palopó', 7),
('San Antonio Palopó', 7),
('San Lucas Tolimán', 7),
('Santa Cruz La Laguna', 7),
('San Pablo La Laguna', 7),
('San Marcos La Laguna', 7),
('San Juan La Laguna', 7),
('San Pedro La Laguna', 7),
('Santiago Atitlán', 7);

INSERT INTO municipality (name, department_id) VALUES
('Totonicapán', 8),
('San Cristóbal Totonicapán', 8),
('San Francisco El Alto', 8),
('San Andrés Xecul', 8),
('Momostenango', 8),
('Santa María Chiquimula', 8),
('Santa Lucía La Reforma', 8),
('San Bartolo', 8);

INSERT INTO municipality (name, department_id) VALUES
('Quetzaltenango', 9),
('Salcajá', 9),
('Olintepeque', 9),
('San Carlos Sija', 9),
('Sibilia', 9),
('Cabricán', 9),
('Cajolá', 9),
('San Miguel Sigüilá', 9),
('Ostuncalco', 9),
('San Mateo', 9),
('Concepción Chiquirichapa', 9),
('San Martín Sacatepéquez', 9),
('Almolonga', 9),
('Cantel', 9),
('Huitán', 9),
('Zunil', 9),
('Colomba Costa Cuca', 9),
('San Francisco La Unión', 9),
('El Palmar', 9),
('Coatepeque', 9),
('Génova', 9),
('Flores Costa Cuca', 9),
('La Esperanza', 9),
('Palestina de Los Altos', 9);

INSERT INTO municipality (name, department_id) VALUES
('Mazatenango', 10),
('Cuyotenango', 10),
('San Francisco Zapotitlán', 10),
('San Bernardino', 10),
('San José El Ídolo', 10),
('Santo Domingo Suchitepéquez', 10),
('San Lorenzo', 10),
('Samayac', 10),
('San Pablo Jocopilas', 10),
('San Antonio Suchitepéquez', 10),
('San Miguel Panán', 10),
('San Gabriel', 10),
('Chicacao', 10),
('Patulul', 10),
('Santa Bárbara', 10),
('San Juan Bautista', 10),
('Santo Tomás La Unión', 10),
('Zunilito', 10),
('Pueblo Nuevo', 10),
('Río Bravo', 10),
('San José La Máquina', 10);

INSERT INTO municipality (name, department_id) VALUES
('Retalhuleu', 11),
('San Sebastián', 11),
('Santa Cruz Muluá', 11),
('San Martín Zapotitlán', 11),
('San Felipe', 11),
('San Andrés Villa Seca', 11),
('Champerico', 11),
('Nuevo San Carlos', 11),
('El Asintal', 11);

INSERT INTO municipality (name, department_id) VALUES
('San Marcos', 12),
('San Pedro Sacatepéquez', 12),
('San Antonio Sacatepéquez', 12),
('Comitancillo', 12),
('San Miguel Ixtahuacán', 12),
('Concepción Tutuapa', 12),
('Tacaná', 12),
('Sibinal', 12),
('Tajumulco', 12),
('Tejutla', 12),
('San Rafael Pie de la Cuesta', 12),
('Nuevo Progreso', 12),
('El Tumbador', 12),
('El Rodeo', 12),
('Malacatán', 12),
('Catarina', 12),
('Ayutla', 12),
('Ocós', 12),
('San Pablo', 12),
('El Quetzal', 12),
('La Reforma', 12),
('Pajapita', 12),
('Ixchiguán', 12),
('San José Ojetenam', 12),
('San Cristóbal Cucho', 12),
('Sipacapa', 12),
('Esquipulas Palo Gordo', 12),
('Río Blanco', 12),
('San Lorenzo', 12),
('La Blanca', 12);

INSERT INTO municipality (name, department_id) VALUES
('Huehuetenango', 13),
('Chiantla', 13),
('Malacatancito', 13),
('Cuilco', 13),
('Nentón', 13),
('San Pedro Necta', 13),
('Jacaltenango', 13),
('San Pedro Soloma', 13),
('San Ildefonso Ixtahuacán', 13),
('Santa Bárbara', 13),
('La Libertad', 13),
('La Democracia', 13),
('San Miguel Acatán', 13),
('San Rafael La Independencia', 13),
('Todos Santos Cuchumatán', 13),
('San Juan Atitán', 13),
('Santa Eulalia', 13),
('San Mateo Ixtatán', 13),
('Colotenango', 13),
('San Sebastián Huehuetenango', 13),
('San Rafael Petzal', 13),
('San Gaspar Ixchil', 13),
('Santiago Chimaltenango', 13),
('Santa Ana Huista', 13),
('Unión Cantinil', 13),
('Aguacatán', 13),
('San Antonio Huista', 13),
('San Sebastián Coatán', 13),
('Tectiután', 13),
('Concepción Huista', 13),
('San Juan Ixcoy', 13),
('Petatán', 13),
('Santa Cruz Barillas', 13);

INSERT INTO municipality (name, department_id) VALUES
('Santa Cruz del Quiché', 14),
('Chiché', 14),
('Chinique', 14),
('Zacualpa', 14),
('Chajul', 14),
('Santo Tomás Chichicastenango', 14),
('Patzité', 14),
('San Antonio Ilotenango', 14),
('San Pedro Jocopilas', 14),
('Cunén', 14),
('San Juan Cotzal', 14),
('Joyabaj', 14),
('Nebaj', 14),
('San Andrés Sajcabajá', 14),
('San Miguel Uspantán', 14),
('Sacapulas', 14),
('San Bartolomé Jocotenango', 14),
('Canillá', 14),
('Chicamán', 14),
('Ixcán', 14),
('Pachalum', 14);

INSERT INTO municipality (name, department_id) VALUES
('Salamá', 15),
('San Miguel Chicaj', 15),
('Rabinal', 15),
('Cubulco', 15),
('Granados', 15),
('Santa Cruz El Chol', 15),
('San Jerónimo', 15),
('Purulhá', 15);

INSERT INTO municipality (name, department_id) VALUES
('Cobán', 16),
('Santa Cruz Verapaz', 16),
('San Cristóbal Verapaz', 16),
('Tactic', 16),
('Tamahú', 16),
('Tucurú', 16),
('Panzós', 16),
('Senahú', 16),
('San Pedro Carchá', 16),
('San Juan Chamelco', 16),
('Lanquín', 16),
('Cahabón', 16),
('Chisec', 16),
('Chahal', 16),
('Fray Bartolomé de las Casas', 16),
('Santa Catalina La Tinta', 16),
('Raxruhá', 16);

INSERT INTO municipality (name, department_id) VALUES
('Flores', 17),
('San José', 17),
('San Benito', 17),
('San Andrés', 17),
('La Libertad', 17),
('San Francisco', 17),
('Santa Ana', 17),
('Dolores', 17),
('San Luis', 17),
('Sayaxché', 17),
('Melchor de Mencos', 17),
('Poptún', 17),
('Las Cruces', 17),
('El Chal', 17);

INSERT INTO municipality (name, department_id) VALUES
('Puerto Barrios', 18),
('Livingston', 18),
('El Estor', 18),
('Morales', 18),
('Los Amates', 18);

INSERT INTO municipality (name, department_id) VALUES
('Zacapa', 19),
('Estanzuela', 19),
('Río Hondo', 19),
('Gualán', 19),
('Teculután', 19),
('Usumatlán', 19),
('Cabañas', 19),
('San Diego', 19),
('La Unión', 19),
('Huité', 19),
('San Jorge', 19);

INSERT INTO municipality (name, department_id) VALUES
('Chiquimula', 20),
('San José La Arada', 20),
('San Juan Ermita', 20),
('Jocotán', 20),
('Camotán', 20),
('Olopa', 20),
('Esquipulas', 20),
('Concepción Las Minas', 20),
('Quezaltepeque', 20),
('San Jacinto', 20),
('Ipala', 20);

INSERT INTO municipality (name, department_id) VALUES
('Jalapa', 21),
('San Pedro Pinula', 21),
('San Luis Jilotepeque', 21),
('San Manuel Chaparrón', 21),
('San Carlos Alzatate', 21),
('Monjas', 21),
('Mataquescuintla', 21);

INSERT INTO municipality (name, department_id) VALUES
('Jutiapa', 22),
('El Progreso', 22),
('Santa Catarina Mita', 22),
('Agua Blanca', 22),
('Asunción Mita', 22),
('Yupiltepeque', 22),
('Atescatempa', 22),
('Jerez', 22),
('El Adelanto', 22),
('Zapotitlán', 22),
('Comapa', 22),
('Jalpatagua', 22),
('Conguaco', 22),
('Moyuta', 22),
('Pasaco', 22),
('San José Acatempa', 22),
('Quesada', 22);

INSERT INTO country (name) VALUES
('Alemania'),
('Estados Unidos'),
('Japón'),
('Corea del Sur'),
('Francia'),
('Italia'),
('Reino Unido'),
('Suecia'),
('República Checa'),
('España'),
('India'),
('China'),
('Brasil'),
('México');

INSERT INTO engine_size (size, description) VALUES
(1.0, 'Motor 1.0 litros'),
(1.2, 'Motor 1.2 litros'),
(1.4, 'Motor 1.4 litros'),
(1.6, 'Motor 1.6 litros'),
(1.8, 'Motor 1.8 litros'),
(2.0, 'Motor 2.0 litros'),
(2.2, 'Motor 2.2 litros'),
(2.4, 'Motor 2.4 litros'),
(2.5, 'Motor 2.5 litros'),
(3.0, 'Motor 3.0 litros'),
(3.5, 'Motor 3.5 litros'),
(4.0, 'Motor 4.0 litros'),
(5.0, 'Motor 5.0 litros'),
(6.0, 'Motor 6.0 litros');

INSERT INTO transmission_type (name, description) VALUES
('Manual', 'Transmisión manual estándar'),
('Automática', 'Transmisión automática'),
('Cvt', 'Transmisión variable continua'),
('Dsg', 'Transmisión de doble embrague'),
('Tiptronic', 'Transmisión automática con modo manual'),
('Secuencial', 'Transmisión secuencial');

INSERT INTO fuel_type (name, description) VALUES
('Gasolina', 'Motor a gasolina'),
('Diésel', 'Motor diésel'),
('Híbrido', 'Motor híbrido gasolina-eléctrico'),
('Eléctrico', 'Motor completamente eléctrico'),
('Gas', 'Motor a gas licuado (GLP)'),
('Etanol', 'Motor que funciona con etanol'),
('Biodiesel', 'Motor que funciona con biodiesel');

INSERT INTO vehicle_brand (name, country_id) VALUES
('Volkswagen', 1),
('Audi', 1),
('Bmw', 1),
('Mercedes-benz', 1),
('Porsche', 1),
('Opel', 1),
('Ford', 2),
('Chevrolet', 2),
('Chrysler', 2),
('Jeep', 2),
('Cadillac', 2),
('Toyota', 3),
('Honda', 3),
('Nissan', 3),
('Mazda', 3),
('Mitsubishi', 3),
('Subaru', 3),
('Suzuki', 3),
('Lexus', 3),
('Infiniti', 3),
('Hyundai', 4),
('Kia', 4),
('Peugeot', 5),
('Renault', 5),
('Citroën', 5),
('Fiat', 6),
('Alfa Romeo', 6),
('Ferrari', 6),
('Land Rover', 7),
('Jaguar', 7),
('Volvo', 8),
('Skoda', 9),
('Seat', 10),
('Tata', 11),
('Chery', 12),
('Geely', 12);

INSERT INTO service_type (name, description) VALUES
('Correctivo', 'Trabajo de reparación por fallas detectadas'),
('Preventivo', 'Mantenimiento programado y prevención');

INSERT INTO work_status (name, description) VALUES
('Pendiente', 'Trabajo creado pero no asignado'),
('Asignado', 'Trabajo asignado pero no iniciado'),
('En progreso', 'Trabajo actualmente en ejecución'),
('Completado', 'Trabajo finalizado exitosamente'),
('Cancelado', 'Trabajo cancelado por cliente o sistema'),
('Finalizado sin ejecución', 'Evaluación completada pero cliente declinó servicio');

INSERT INTO payment_status (name, description) VALUES
('Pendiente', 'Pago no recibido aún'),
('Parcial', 'Pago parcial recibido'),
('Pagado', 'Pago completo recibido'),
('Vencido', 'Pago vencido');

INSERT INTO purchase_order_status (name, description) VALUES
('Pendiente', 'Orden creada pero no enviada'),
('Enviada', 'Orden enviada al proveedor'),
('Confirmada', 'Proveedor confirmó la orden'),
('Entregada', 'Orden entregada'),
('Cancelada', 'Orden cancelada');

INSERT INTO part_category (name, description) VALUES
('Motor', 'Componentes y partes del motor'),
('Frenos', 'Componentes del sistema de frenos'),
('Sistema eléctrico', 'Componentes del sistema eléctrico'),
('Transmisión', 'Componentes de transmisión'),
('Suspensión', 'Partes del sistema de suspensión'),
('Filtros', 'Filtros de aire, aceite y combustible'),
('Fluidos', 'Aceites, refrigerantes, líquido de frenos'),
('Llantas', 'Llantas y componentes de ruedas'),
('Carrocería', 'Paneles y partes exteriores'),
('Interior', 'Componentes y accesorios del interior'),
('Escape', 'Sistema de escape'),
('Climatización', 'Sistema de aire acondicionado'),
('Dirección', 'Componentes del sistema de dirección'),
('Combustible', 'Sistema de combustible'),
('Refrigeración', 'Sistema de refrigeración del motor');

INSERT INTO specialization_type (name, description) VALUES
('Electricidad automotriz', 'Sistemas eléctricos del vehículo'),
('Diagnóstico computarizado', 'Diagnóstico de computadoras y ECU del vehículo'),
('Sistemas de frenos', 'Especialista en sistemas de frenos'),
('Reparación de motores', 'Reparación y mantenimiento de motores'),
('Transmisión', 'Sistemas de transmisión'),
('Aire acondicionado', 'Sistemas de AC y climatización del vehículo'),
('Suspensión', 'Sistemas de suspensión'),
('Dirección', 'Sistemas de dirección'),
('Inyección electrónica', 'Sistemas de inyección de combustible'),
('Turbo y sobrealimentación', 'Sistemas turbo y de sobrealimentación'),
('Híbridos', 'Vehículos híbridos'),
('Diésel', 'Motores diésel'),
('Carrocería', 'Reparación de carrocería'),
('Pintura', 'Pintura automotriz');

INSERT INTO payment_method (name, description) VALUES
('Efectivo', 'Pago en efectivo'),
('Tarjeta de crédito', 'Pago con tarjeta de crédito'),
('Tarjeta de débito', 'Pago con tarjeta de débito'),
('Transferencia bancaria', 'Transferencia bancaria'),
('Cheque', 'Pago con cheque'),
('Depósito bancario', 'Depósito directo en cuenta'),
('Pago móvil', 'Pago a través de aplicación móvil'),
('Cryptocurrency', 'Pago con criptomonedas');

INSERT INTO movement_type (name, description) VALUES
('Entrada', 'Movimiento de entrada de inventario'),
('Salida', 'Movimiento de salida de inventario'),
('Ajuste', 'Ajuste de inventario'),
('Transferencia', 'Transferencia entre ubicaciones'),
('Devolución', 'Devolución de producto'),
('Merma', 'Pérdida o deterioro de producto');

INSERT INTO reference_type (name, description) VALUES
('Orden de compra', 'Referencia a orden de compra'),
('Trabajo', 'Referencia a orden de trabajo'),
('Ajuste', 'Referencia a ajuste manual'),
('Transferencia', 'Referencia a transferencia'),
('Devolución', 'Referencia a devolución'),
('Inventario inicial', 'Referencia a inventario inicial');

INSERT INTO vehicle_model (name, brand_id, year, engine_size_id, transmission_type_id, fuel_type_id) VALUES
('Corolla', 12, 2023, 4, 2, 1),
('Camry', 12, 2023, 6, 2, 1),
('Hilux', 12, 2023, 7, 1, 2),
('Prado', 12, 2023, 10, 2, 1),
('Rav4', 12, 2023, 6, 2, 1),
('Civic', 13, 2023, 4, 3, 1),
('Accord', 13, 2023, 6, 2, 1),
('Cr-v', 13, 2023, 4, 3, 1),
('Pilot', 13, 2023, 10, 2, 1),
('Sentra', 14, 2023, 4, 3, 1),
('Altima', 14, 2023, 7, 3, 1),
('X-trail', 14, 2023, 7, 3, 1),
('Frontier', 14, 2023, 7, 1, 1),
('Elantra', 21, 2023, 6, 2, 1),
('Tucson', 21, 2023, 6, 2, 1),
('Santa Fe', 21, 2023, 8, 2, 1),
('Rio', 22, 2023, 3, 1, 1),
('Forte', 22, 2023, 6, 2, 1),
('Sportage', 22, 2023, 6, 2, 1),
('Sorento', 22, 2023, 8, 2, 1),
('Focus', 7, 2023, 5, 2, 1),
('Escape', 7, 2023, 4, 2, 1),
('Explorer', 7, 2023, 8, 2, 1),
('F-150', 7, 2023, 10, 2, 1),
('Spark', 8, 2023, 3, 1, 1),
('Aveo', 8, 2023, 3, 1, 1),
('Cruze', 8, 2023, 4, 2, 1),
('Equinox', 8, 2023, 4, 2, 1),
('Jetta', 1, 2023, 4, 2, 1),
('Passat', 1, 2023, 6, 2, 1),
('Tiguan', 1, 2023, 6, 2, 1),
('Golf', 1, 2023, 4, 1, 1);

INSERT INTO part (name, description, category_id, unit_price, minimum_stock) VALUES
('Filtro de aceite', 'Filtro de aceite para motor', 6, 25.00, 50),
('Filtro de aire', 'Filtro de aire del motor', 6, 35.00, 30),
('Filtro de combustible', 'Filtro de combustible', 6, 45.00, 25),
('Filtro de cabina', 'Filtro del aire acondicionado', 6, 40.00, 20),
('Aceite de motor 5w30', 'Aceite sintético para motor', 7, 180.00, 30),
('Aceite de motor 10w40', 'Aceite semi-sintético para motor', 7, 150.00, 25),
('Líquido de frenos', 'Líquido de frenos DOT 3', 7, 65.00, 15),
('Refrigerante', 'Refrigerante para radiador', 7, 85.00, 20),
('Pastillas de freno delanteras', 'Pastillas de freno para eje delantero', 2, 180.00, 20),
('Pastillas de freno traseras', 'Pastillas de freno para eje trasero', 2, 150.00, 20),
('Discos de freno delanteros', 'Discos de freno para eje delantero', 2, 320.00, 10),
('Discos de freno traseros', 'Discos de freno para eje trasero', 2, 280.00, 10),
('Bujías', 'Bujías de encendido', 1, 45.00, 40),
('Correa de distribución', 'Correa de distribución del motor', 1, 220.00, 15),
('Bomba de agua', 'Bomba de agua del motor', 1, 380.00, 8),
('Termostato', 'Termostato del motor', 1, 95.00, 12),
('Batería', 'Batería de 12V para vehículo', 3, 650.00, 8),
('Alternador', 'Alternador del vehículo', 3, 850.00, 5),
('Motor de arranque', 'Motor de arranque', 3, 750.00, 5),
('Fusibles', 'Set de fusibles variados', 3, 35.00, 25),
('Amortiguadores delanteros', 'Par de amortiguadores delanteros', 5, 480.00, 10),
('Amortiguadores traseros', 'Par de amortiguadores traseros', 5, 450.00, 10),
('Resortes delanteros', 'Par de resortes delanteros', 5, 280.00, 8),
('Resortes traseros', 'Par de resortes traseros', 5, 260.00, 8);

-- Insertar personas para los usuarios de prueba
INSERT INTO person (cui, nit, first_name, last_name, email, phone, birth_date, gender_id) VALUES
-- Administradores
('3140315290901', '12345678-9', 'Carlos', 'González', 'diegomaldonado201931811@cunoc.edu.gt', '50211234567', '1985-03-15', 1),
('1234567890102', '12345679-0', 'María', 'López', 'admin2@tallermec.com', '50211234568', '1987-08-22', 2),

-- Empleados
('1234567890103', '12345680-1', 'José', 'Pérez', 'dj_maldonado19@hotmail.es', '50211234569', '1990-11-10', 1),
('1234567890104', '12345681-2', 'Ana', 'Martínez', 'empleado2@tallermec.com', '50211234570', '1992-05-18', 2),

-- Especialistas
('1234567890105', '12345682-3', 'Roberto', 'García', 'dmaldonado@cari.net', '50211234571', '1988-07-25', 1),
('1234567890106', '12345683-4', 'Laura', 'Rodríguez', 'especialista2@tallermec.com', '50211234572', '1991-12-03', 2),

-- Clientes
('1234567890107', '12345684-5', 'Miguel', 'Torres', 'karinmonte@hotmail.com', '50211234573', '1995-02-14', 1),
('1234567890108', '12345685-6', 'Carmen', 'Flores', 'cliente2@gmail.com', '50211234574', '1993-09-07', 2),

-- Proveedores
('1234567890109', '12345686-7', 'Luis', 'Morales', 'rebecahi28@gmail.com', '50211234575', '1980-04-30', 1),
('1234567890110', '12345687-8', 'Patricia', 'Vargas', 'proveedor2@autopartes.com', '50211234576', '1983-10-12', 2);

INSERT INTO "user" (person_cui, user_type_id, username, password, is_active) VALUES
-- Administradores (user_type_id = 1)
('3140315290901', 1, 'admin1', 'admin123', true), -- password: admin123
('1234567890102', 1, 'admin2', 'admin123', true), -- password: admin123

-- Empleados (user_type_id = 2)
('1234567890103', 2, 'empleado1', 'empleado123', true), -- password: empleado123
('1234567890104', 2, 'empleado2', 'empleado123', true), -- password: empleado123

-- Especialistas (user_type_id = 3)
('1234567890105', 3, 'especialista1', 'especialista123', true), -- password: especialista123
('1234567890106', 3, 'especialista2', 'especialista123', true), -- password: especialista123

-- Clientes (user_type_id = 4)
('1234567890107', 4, 'cliente1', 'cliente123', true), -- password: cliente123
('1234567890108', 4, 'cliente2', 'cliente123', true), -- password: cliente123

-- Proveedores (user_type_id = 5)
('1234567890109', 5, 'proveedor1', 'proveedor123', true), -- password: proveedor123
('1234567890110', 5, 'proveedor2', 'proveedor123', true); -- password: proveedor123