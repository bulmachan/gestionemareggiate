
CREATE TABLE IF NOT EXISTS MRG_F_DANNI_LIN
(
  "OBJECTID" bigint NOT NULL,
  "GISID" bigint NOT NULL,
  "SHAPE" geometry,
  CONSTRAINT mrg_f_danni_lin_pkey PRIMARY KEY ("OBJECTID")
);


CREATE TABLE IF NOT EXISTS MRG_F_DANNI_POL
(
  "OBJECTID" bigint NOT NULL,
  "GISID" bigint NOT NULL,
  "SHAPE" geometry,
  CONSTRAINT mrg_f_danni_pol_pkey PRIMARY KEY ("OBJECTID")
);


CREATE TABLE IF NOT EXISTS MRG_F_DANNI_PUN
(
  "OBJECTID" bigint NOT NULL,
  "GISID" bigint NOT NULL,
  "SHAPE" geometry,
  CONSTRAINT mrg_f_danni_pun_pkey PRIMARY KEY ("OBJECTID")
);


delete from  MRG_MAREOGRAFI;
delete from  MRG_BOE;
delete from  MRG_TENDENZE;
delete from  MRG_PROVENIENZE;
delete from  MRG_TIPI_MACRO_AREE;
delete from  MRG_TIPI_ALLEGATO;
delete from  MRG_ESTENSIONI;
delete from  MRG_STATI_RELAZIONI;
delete from  MRG_LOCALITA;
delete from MRG_TIPI_DANNO;
delete from MRG_STB;
delete from MRG_STAZIONE_VARIABILE;
delete from MRG_STAZIONI;
delete from MRG_VARIABILI;



INSERT  INTO MRG_TENDENZE VALUES(1, 'Esaurimento');
INSERT  INTO MRG_TENDENZE VALUES(2, 'Stazionarieta');

INSERT  INTO MRG_PROVENIENZE VALUES(1, 'NNE');
INSERT  INTO MRG_PROVENIENZE VALUES(2, 'NE');
INSERT  INTO MRG_PROVENIENZE VALUES(3, 'S');

INSERT  INTO MRG_TIPI_MACRO_AREE VALUES(1, 'B1');
INSERT  INTO MRG_TIPI_MACRO_AREE VALUES(2, 'B2');
INSERT  INTO MRG_TIPI_MACRO_AREE VALUES(3, 'D');

INSERT INTO MRG_COMUNI (ID_COMUNE,ID_MACROAREA,nome) values (38005,3,'Ravenna');
INSERT INTO MRG_COMUNI (ID_COMUNE,ID_MACROAREA,nome) values (38006,3,'Ravenna');
INSERT INTO MRG_COMUNI (ID_COMUNE,ID_MACROAREA,nome) values (38025,3,'Ravenna');

INSERT  INTO MRG_TIPI_ALLEGATO VALUES(1, 'Bollettino ARPA');
INSERT  INTO MRG_TIPI_ALLEGATO VALUES(2, 'Quotidiano');
INSERT  INTO MRG_TIPI_ALLEGATO VALUES(3, 'Immagine');

INSERT  INTO MRG_ESTENSIONI VALUES(1, 'Regionale');
INSERT  INTO MRG_ESTENSIONI VALUES(2, 'Nazionale');

INSERT  INTO MRG_TIPI_DANNO VALUES(1, 'Erosione');
INSERT  INTO MRG_TIPI_DANNO VALUES(2, 'Tracimazione Canali');

INSERT  INTO MRG_LOCALITA VALUES(1, 'Goro' ,'',38005);
INSERT  INTO MRG_LOCALITA VALUES(2, 'Ravenna','',38005);

INSERT  INTO MRG_STATI_RELAZIONI VALUES(1, 'Protocollato');
INSERT  INTO MRG_STATI_RELAZIONI VALUES(2, 'Definitivo');
INSERT  INTO MRG_STATI_RELAZIONI VALUES(3, 'Bozza');

INSERT  INTO MRG_STB VALUES(1, 'STB fiumi romagnoli');
INSERT  INTO MRG_STB VALUES(2, 'STB Po di Volano e della Costa');

INSERT INTO MRG_FENOMENI VALUES(1,'Livello del mare sopra soglia');
INSERT INTO MRG_FENOMENI VALUES(2,'Livello onda sopra soglia');

INSERT INTO MRG_ALTEZZE VALUES(1,'1,25 a 2,5 (Molto Mosso)');
INSERT INTO MRG_ALTEZZE VALUES(2,'Da 2,5 a 4 (Agitato)');


INSERT INTO MRG_LIVELLO_CRITICITA VALUES(1,'livello 1');
INSERT INTO MRG_LIVELLO_CRITICITA VALUES(2,'livello 2');


INSERT INTO MRG_INDIRIZZI VALUES(1,'All''Assessore Sicurezza territoriale, difesa del suolo e della costa, protezione civile','Via della Fiera, 8 40127 Bologna');
INSERT INTO MRG_INDIRIZZI VALUES(2,'Al Direttore generale ambiente e difesa del suolo e della costa','Via della Fiera, 8 40127 Bologna');
INSERT INTO MRG_INDIRIZZI VALUES(3,'Al Responsabile del Servizio difesa del suolo, della costa e bonifica','Via della Fiera, 8 40127 Bologna');
INSERT INTO MRG_INDIRIZZI VALUES(4,'Al Responsabile dell''Agenzia di Protezione Civile','Viale Silvani 6, 40122 Bologna');




