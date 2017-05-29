-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- ***********************************  Create tables ************************************* 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- tes2_BuildingUnit 
-- -------------------------------------------------------------------- 
CREATE TABLE tes2_BuildingUnit
(
    ID INTEGER NOT NULL,
    BuildingUnit_Parent_ID INTEGER,
    BuildingUnit_Root_ID INTEGER,
    building_buildingUnit_ID INTEGER,
    class_uom VARCHAR(254),
    class VARCHAR(254),
    usage_uom VARCHAR(254),
    usage VARCHAR(254),
    function_uom VARCHAR(254),
    function VARCHAR(254),
    lod2MultiCurve geometry(GEOMETRYZ),
    lod3MultiCurve geometry(GEOMETRYZ),
    lod4MultiCurve geometry(GEOMETRYZ),
    lod1MultiSurface_ID INTEGER,
    lod2MultiSurface_ID INTEGER,
    lod3MultiSurface_ID INTEGER,
    lod4MultiSurface_ID INTEGER,
    lod1Solid_ID INTEGER,
    lod2Solid_ID INTEGER,
    lod3Solid_ID INTEGER,
    lod4Solid_ID INTEGER,
    PRIMARY KEY (ID)
);

-- -------------------------------------------------------------------- 
-- tes2_BuildingUnit_to_address 
-- -------------------------------------------------------------------- 
CREATE TABLE tes2_BuildingUnit_to_address
(
    BuildingUnit_ID INTEGER NOT NULL,
    address_ID INTEGER NOT NULL,
    PRIMARY KEY (BuildingUnit_ID, address_ID)
);

-- -------------------------------------------------------------------- 
-- tes2_EnergyPerformanceCertific 
-- -------------------------------------------------------------------- 
CREATE TABLE tes2_EnergyPerformanceCertific
(
    ID INTEGER NOT NULL,
    BuildingUnit_energyPerforma_ID INTEGER,
    certificationName VARCHAR(254),
    certificationid VARCHAR(254),
    PRIMARY KEY (ID)
);

-- -------------------------------------------------------------------- 
-- tes2_Facilities 
-- -------------------------------------------------------------------- 
CREATE TABLE tes2_Facilities
(
    ID INTEGER NOT NULL,
    BuildingUnit_equippedWith_ID INTEGER,
    totalValue_uom VARCHAR(254),
    totalValue NUMERIC,
    PRIMARY KEY (ID)
);

-- -------------------------------------------------------------------- 
-- tes2_IndustrialBuilding 
-- -------------------------------------------------------------------- 
CREATE TABLE tes2_IndustrialBuilding
(
    ID INTEGER NOT NULL,
    remark VARCHAR(254),
    PRIMARY KEY (ID)
);

-- -------------------------------------------------------------------- 
-- tes2_building 
-- -------------------------------------------------------------------- 
CREATE TABLE tes2_building
(
    ID INTEGER NOT NULL,
    ownerName VARCHAR(254),
    floorArea_uom VARCHAR(254),
    floorArea NUMERIC,
    EnergyPerforman_certificationN VARCHAR(254),
    EnergyPerforman_certificationi VARCHAR(254),
    PRIMARY KEY (ID)
);

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************  Create foreign keys  ******************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- tes2_BuildingUnit 
-- -------------------------------------------------------------------- 
ALTER TABLE tes2_BuildingUnit
    ADD CONSTRAINT tes2_BuildingUnit_FK FOREIGN KEY (ID) REFERENCES cityobject (ID);

ALTER TABLE tes2_BuildingUnit
    ADD CONSTRAINT tes2_BuildingUnit_Parent_FK FOREIGN KEY (BuildingUnit_Parent_ID) REFERENCES tes2_BuildingUnit (ID);

ALTER TABLE tes2_BuildingUnit
    ADD CONSTRAINT tes2_BuildingUnit_Root_FK FOREIGN KEY (BuildingUnit_Root_ID) REFERENCES tes2_BuildingUnit (ID);

ALTER TABLE tes2_BuildingUnit
    ADD CONSTRAINT tes2_Buildin_buildin_buildi_FK FOREIGN KEY (building_buildingUnit_ID) REFERENCES tes2_building (ID);

ALTER TABLE tes2_BuildingUnit
    ADD CONSTRAINT tes2_BuildingUni_lod1MultiS_FK FOREIGN KEY (lod1MultiSurface_ID) REFERENCES SURFACE_GEOMETRY (ID);

ALTER TABLE tes2_BuildingUnit
    ADD CONSTRAINT tes2_BuildingUni_lod2MultiS_FK FOREIGN KEY (lod2MultiSurface_ID) REFERENCES SURFACE_GEOMETRY (ID);

ALTER TABLE tes2_BuildingUnit
    ADD CONSTRAINT tes2_BuildingUni_lod3MultiS_FK FOREIGN KEY (lod3MultiSurface_ID) REFERENCES SURFACE_GEOMETRY (ID);

ALTER TABLE tes2_BuildingUnit
    ADD CONSTRAINT tes2_BuildingUni_lod4MultiS_FK FOREIGN KEY (lod4MultiSurface_ID) REFERENCES SURFACE_GEOMETRY (ID);

ALTER TABLE tes2_BuildingUnit
    ADD CONSTRAINT tes2_BuildingUnit_lod1Solid_FK FOREIGN KEY (lod1Solid_ID) REFERENCES SURFACE_GEOMETRY (ID);

ALTER TABLE tes2_BuildingUnit
    ADD CONSTRAINT tes2_BuildingUnit_lod2Solid_FK FOREIGN KEY (lod2Solid_ID) REFERENCES SURFACE_GEOMETRY (ID);

ALTER TABLE tes2_BuildingUnit
    ADD CONSTRAINT tes2_BuildingUnit_lod3Solid_FK FOREIGN KEY (lod3Solid_ID) REFERENCES SURFACE_GEOMETRY (ID);

ALTER TABLE tes2_BuildingUnit
    ADD CONSTRAINT tes2_BuildingUnit_lod4Solid_FK FOREIGN KEY (lod4Solid_ID) REFERENCES SURFACE_GEOMETRY (ID);

-- -------------------------------------------------------------------- 
-- tes2_BuildingUnit_to_address 
-- -------------------------------------------------------------------- 
ALTER TABLE tes2_BuildingUnit_to_address
    ADD CONSTRAINT tes2_BuildingUn_to_address_FK1 FOREIGN KEY (BuildingUnit_ID) REFERENCES tes2_BuildingUnit (ID);

ALTER TABLE tes2_BuildingUnit_to_address
    ADD CONSTRAINT tes2_BuildingUn_to_address_FK2 FOREIGN KEY (address_ID) REFERENCES address (ID);

-- -------------------------------------------------------------------- 
-- tes2_EnergyPerformanceCertific 
-- -------------------------------------------------------------------- 
ALTER TABLE tes2_EnergyPerformanceCertific
    ADD CONSTRAINT tes2_EnergyP_Buildin_energy_FK FOREIGN KEY (BuildingUnit_energyPerforma_ID) REFERENCES tes2_BuildingUnit (ID);

-- -------------------------------------------------------------------- 
-- tes2_Facilities 
-- -------------------------------------------------------------------- 
ALTER TABLE tes2_Facilities
    ADD CONSTRAINT tes2_Facilit_Buildin_equipp_FK FOREIGN KEY (BuildingUnit_equippedWith_ID) REFERENCES tes2_BuildingUnit (ID);

-- -------------------------------------------------------------------- 
-- tes2_IndustrialBuilding 
-- -------------------------------------------------------------------- 
ALTER TABLE tes2_IndustrialBuilding
    ADD CONSTRAINT tes2_IndustrialBuilding_FK FOREIGN KEY (ID) REFERENCES building (ID);

-- -------------------------------------------------------------------- 
-- tes2_building 
-- -------------------------------------------------------------------- 
ALTER TABLE tes2_building
    ADD CONSTRAINT tes2_building_FK FOREIGN KEY (ID) REFERENCES building (ID);

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************  Create Indexes  ************************************* 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- tes2_BuildingUnit 
-- -------------------------------------------------------------------- 
CREATE INDEX tes2_BuildingUnit_Parent_FKX ON tes2_BuildingUnit
    USING btree
    (
      BuildingUnit_Parent_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX tes2_BuildingUnit_Root_FKX ON tes2_BuildingUnit
    USING btree
    (
      BuildingUnit_Root_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX tes2_Buildin_buildi_buildi_FKX ON tes2_BuildingUnit
    USING btree
    (
      building_buildingUnit_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX tes2_BuildingUn_lod2MultiC_SPX ON tes2_BuildingUnit
    USING gist
    (
      lod2MultiCurve
    );

CREATE INDEX tes2_BuildingUn_lod3MultiC_SPX ON tes2_BuildingUnit
    USING gist
    (
      lod3MultiCurve
    );

CREATE INDEX tes2_BuildingUn_lod4MultiC_SPX ON tes2_BuildingUnit
    USING gist
    (
      lod4MultiCurve
    );

CREATE INDEX tes2_BuildingUn_lod1MultiS_FKX ON tes2_BuildingUnit
    USING btree
    (
      lod1MultiSurface_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX tes2_BuildingUn_lod2MultiS_FKX ON tes2_BuildingUnit
    USING btree
    (
      lod2MultiSurface_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX tes2_BuildingUn_lod3MultiS_FKX ON tes2_BuildingUnit
    USING btree
    (
      lod3MultiSurface_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX tes2_BuildingUn_lod4MultiS_FKX ON tes2_BuildingUnit
    USING btree
    (
      lod4MultiSurface_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX tes2_BuildingUni_lod1Solid_FKX ON tes2_BuildingUnit
    USING btree
    (
      lod1Solid_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX tes2_BuildingUni_lod2Solid_FKX ON tes2_BuildingUnit
    USING btree
    (
      lod2Solid_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX tes2_BuildingUni_lod3Solid_FKX ON tes2_BuildingUnit
    USING btree
    (
      lod3Solid_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX tes2_BuildingUni_lod4Solid_FKX ON tes2_BuildingUnit
    USING btree
    (
      lod4Solid_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- tes2_EnergyPerformanceCertific 
-- -------------------------------------------------------------------- 
CREATE INDEX tes2_EnergyP_Buildi_energy_FKX ON tes2_EnergyPerformanceCertific
    USING btree
    (
      BuildingUnit_energyPerforma_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- tes2_Facilities 
-- -------------------------------------------------------------------- 
CREATE INDEX tes2_Facilit_Buildi_equipp_FKX ON tes2_Facilities
    USING btree
    (
      BuildingUnit_equippedWith_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************  Create Sequences  *********************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 

CREATE SEQUENCE tes2_EnergyPerformanceCert_SEQ
INCREMENT BY 1
MINVALUE 0
MAXVALUE 2147483647
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;

