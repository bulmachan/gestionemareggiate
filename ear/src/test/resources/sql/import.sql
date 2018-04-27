

--CREATE TABLE  "MRG_F_DANNI_LIN" 
--(	"OBJECTID" integer NOT NULL ENABLE, 
--	"SHAPE" sde.st_geometry NOT NULL ENABLE , 
--	 PRIMARY KEY ("Objectid")   ENABLE
--);


--CREATE TABLE  "MRG_F_DANNI_POL" 
--(	"OBJECTID" integer NOT NULL ENABLE, 
--	"SHAPE" sde.st_geometry NOT NULL ENABLE , 
--	 PRIMARY KEY ("Objectid")   ENABLE
--);



--CREATE TABLE  "MRG_F_DANNI_PUN" 
-- (	"OBJECTID" integer NOT NULL ENABLE, 
--	"SHAPE" sde.st_geometry NOT NULL ENABLE , 
--	 PRIMARY KEY ("Objectid")   ENABLE
--);



INSERT  INTO MRG_TENDENZE VALUES(1, 'Esaurimento');
INSERT  INTO MRG_TENDENZE VALUES(2, 'Attenuazione');
INSERT  INTO MRG_TENDENZE VALUES(3, 'Stazionariet�');
INSERT  INTO MRG_TENDENZE VALUES(4, 'Intensificazione');


INSERT  INTO MRG_PROVENIENZE VALUES(1, 'N');
INSERT  INTO MRG_PROVENIENZE VALUES(2, 'NE');
INSERT  INTO MRG_PROVENIENZE VALUES(3, 'E');
INSERT  INTO MRG_PROVENIENZE VALUES(4, 'SE');
INSERT  INTO MRG_PROVENIENZE VALUES(5, 'S');
Insert into MRG_PROVENIENZE values (6,'O');
Insert into MRG_PROVENIENZE values (7,'SO');
Insert into MRG_PROVENIENZE values (8,'NO');
Insert into MRG_PROVENIENZE values (9,'NNE');
Insert into MRG_PROVENIENZE values (10,'ESE');
Insert into MRG_PROVENIENZE values (11,'SSE');
Insert into MRG_PROVENIENZE values (12,'SSO');
Insert into MRG_PROVENIENZE values (13,'OSO');
Insert into MRG_PROVENIENZE values (14,'ONO');
Insert into MRG_PROVENIENZE values (15,'NNO');



INSERT  INTO MRG_TIPI_MACRO_AREE VALUES(2, 'B2');
INSERT  INTO MRG_TIPI_MACRO_AREE VALUES(3, 'D');

Insert into MRG_TIPI_ALLEGATO (ID,NOME) values ('1','Avviso Meteo');
Insert into MRG_TIPI_ALLEGATO (ID,NOME) values ('2','Allerta di Protezione Civile');
Insert into MRG_TIPI_ALLEGATO (ID,NOME) values ('3','Grafici EWS');
Insert into MRG_TIPI_ALLEGATO (ID,NOME) values ('4','Foto');

Insert into MRG_TIPI_ALLEGATO (ID,NOME) values ('5','Quotidiano');
Insert into MRG_TIPI_ALLEGATO (ID,NOME) values ('6','Segnalazione da Comuni');
Insert into MRG_TIPI_ALLEGATO (ID,NOME) values ('7','Segnalazione da cittadini');
Insert into MRG_TIPI_ALLEGATO (ID,NOME) values ('8','Segnalazione da Giornale eventi APC');
Insert into MRG_TIPI_ALLEGATO (ID,NOME) values ('9','Foto');
Insert into MRG_TIPI_ALLEGATO (ID,NOME) values ('10','Video');

INSERT  INTO MRG_ESTENSIONI VALUES(1, 'Regionale');
INSERT  INTO MRG_ESTENSIONI VALUES(2, 'Nazionale');

Insert into MRG_TIPI_DANNO (ID,NOME) values ('1','Erosioni');
Insert into MRG_TIPI_DANNO (ID,NOME) values ('2','Tracimazioni canali');
Insert into MRG_TIPI_DANNO (ID,NOME) values ('3','Inondazioni');
Insert into MRG_TIPI_DANNO (ID,NOME) values ('4','Danni agli stabilimenti');
Insert into MRG_TIPI_DANNO (ID,NOME) values ('5','Danni alle opere di difesa');



INSERT  INTO MRG_STATI_RELAZIONI VALUES(1, 'Protocollato');
INSERT  INTO MRG_STATI_RELAZIONI VALUES(2, 'Definitivo');
INSERT  INTO MRG_STATI_RELAZIONI VALUES(3, 'Bozza');

INSERT  INTO MRG_STB VALUES(1, 'STB fiumi romagnoli');
INSERT  INTO MRG_STB VALUES(2, 'STB Po di Volano e della Costa');

INSERT INTO MRG_LIVELLO_CRITICITA VALUES(1,'livello 1');
INSERT INTO MRG_LIVELLO_CRITICITA VALUES(2,'livello 2');
INSERT INTO MRG_LIVELLO_CRITICITA VALUES(3,'livello 3');


INSERT INTO MRG_INDIRIZZI VALUES(1,'All''Assessore Sicurezza territoriale, difesa del suolo e della costa, protezione civile','Via della Fiera, 8 40127 Bologna');
INSERT INTO MRG_INDIRIZZI VALUES(2,'Al Direttore generale ambiente e difesa del suolo e della costa','Via della Fiera, 8 40127 Bologna');
INSERT INTO MRG_INDIRIZZI VALUES(3,'Al Responsabile del Servizio difesa del suolo, della costa e bonifica','Via della Fiera, 8 40127 Bologna');
INSERT INTO MRG_INDIRIZZI VALUES(4,'Al Responsabile dell''Agenzia di Protezione Civile','Viale Silvani 6, 40122 Bologna');

INSERT INTO MRG_ALTEZZE VALUES(4,'20');
INSERT INTO MRG_ALTEZZE VALUES(5,'30');
INSERT INTO MRG_ALTEZZE VALUES(6,'40');

INSERT INTO MRG_FENOMENI VALUES(4,'male');
INSERT INTO MRG_FENOMENI VALUES(5,'sto bene');
INSERT INTO MRG_FENOMENI VALUES(6,'spegnete tutto');

COMMIT;