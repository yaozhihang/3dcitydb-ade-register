-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- ***********************************  Create tables ************************************* 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- test_BuildingUnit 
-- -------------------------------------------------------------------- 
CREATE TABLE test_BuildingUnit
(
    ID INTEGER NOT NULL,
    OBJECTCLASS_ID INTEGER,
    building_buildingUnit_ID INTEGER,
    BuildingUnit_Parent_ID INTEGER,
    BuildingUnit_Root_ID INTEGER,
    lod1MultiSurface_ID INTEGER,
    lod2MultiSurface_ID INTEGER,
    lod3MultiSurface_ID INTEGER,
    lod4MultiSurface_ID INTEGER,
    lod1Solid_ID INTEGER,
    lod2Solid_ID INTEGER,
    lod3Solid_ID INTEGER,
    lod4Solid_ID INTEGER,
    class_uom VARCHAR(254),
    class VARCHAR(254),
    usage_uom VARCHAR(254),
    usage VARCHAR(254),
    function_uom VARCHAR(254),
    function VARCHAR(254),
    lod2MultiCurve geometry(GEOMETRYZ),
    lod3MultiCurve geometry(GEOMETRYZ),
    lod4MultiCurve geometry(GEOMETRYZ),
    PRIMARY KEY (ID)
);

-- -------------------------------------------------------------------- 
-- test_BuildingUnit_to_address 
-- -------------------------------------------------------------------- 
CREATE TABLE test_BuildingUnit_to_address
(
    BuildingUnit_ID INTEGER NOT NULL,
    address_ID INTEGER NOT NULL,
    PRIMARY KEY (BuildingUnit_ID, address_ID)
);

-- -------------------------------------------------------------------- 
-- test_EnergyPerformanceCertific 
-- -------------------------------------------------------------------- 
CREATE TABLE test_EnergyPerformanceCertific
(
    ID INTEGER NOT NULL,
    BuildingUnit_energyPerforma_ID INTEGER,
    certificationName VARCHAR(254),
    certificationid VARCHAR(254),
    PRIMARY KEY (ID)
);

-- -------------------------------------------------------------------- 
-- test_Facilities 
-- -------------------------------------------------------------------- 
CREATE TABLE test_Facilities
(
    ID INTEGER NOT NULL,
    OBJECTCLASS_ID INTEGER,
    BuildingUnit_equippedWith_ID INTEGER,
    totalValue_uom VARCHAR(254),
    totalValue NUMERIC,
    PRIMARY KEY (ID)
);

-- -------------------------------------------------------------------- 
-- test_IndustrialBuilding 
-- -------------------------------------------------------------------- 
CREATE TABLE test_IndustrialBuilding
(
    ID INTEGER NOT NULL,
    remark VARCHAR(254),
    PRIMARY KEY (ID)
);

-- -------------------------------------------------------------------- 
-- test_IndustrialBuildingPart 
-- -------------------------------------------------------------------- 
CREATE TABLE test_IndustrialBuildingPart
(
    ID INTEGER NOT NULL,
    remark VARCHAR(254),
    PRIMARY KEY (ID)
);

-- -------------------------------------------------------------------- 
-- test_IndustrialBuildingRoofSur 
-- -------------------------------------------------------------------- 
CREATE TABLE test_IndustrialBuildingRoofSur
(
    ID INTEGER NOT NULL,
    remark VARCHAR(254),
    PRIMARY KEY (ID)
);

-- -------------------------------------------------------------------- 
-- test_OtherCo_to_themati_surfac 
-- -------------------------------------------------------------------- 
CREATE TABLE test_OtherCo_to_themati_surfac
(
    OtherConstruction_ID INTEGER NOT NULL,
    thematic_surface_ID INTEGER NOT NULL,
    PRIMARY KEY (OtherConstruction_ID, thematic_surface_ID)
);

-- -------------------------------------------------------------------- 
-- test_OtherConstruction 
-- -------------------------------------------------------------------- 
CREATE TABLE test_OtherConstruction
(
    ID INTEGER NOT NULL,
    PRIMARY KEY (ID)
);

-- -------------------------------------------------------------------- 
-- test_building 
-- -------------------------------------------------------------------- 
CREATE TABLE test_building
(
    ID INTEGER NOT NULL,
    ownerName VARCHAR(254),
    EnergyPerforman_certificationN VARCHAR(254),
    EnergyPerforman_certificationi VARCHAR(254),
    floorArea_uom VARCHAR(254),
    floorArea NUMERIC,
    PRIMARY KEY (ID)
);

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************  Create foreign keys  ******************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- test_BuildingUnit 
-- -------------------------------------------------------------------- 
ALTER TABLE test_BuildingUnit
    ADD CONSTRAINT test_BuildingUni_Objectclas_FK FOREIGN KEY (OBJECTCLASS_ID) REFERENCES objectclass (ID);

ALTER TABLE test_BuildingUnit
    ADD CONSTRAINT test_BuildingUnit_FK FOREIGN KEY (ID) REFERENCES cityobject (ID);

ALTER TABLE test_BuildingUnit
    ADD CONSTRAINT test_Buildin_buildin_buildi_FK FOREIGN KEY (building_buildingUnit_ID) REFERENCES test_building (ID);

ALTER TABLE test_BuildingUnit
    ADD CONSTRAINT test_BuildingUnit_Parent_FK FOREIGN KEY (BuildingUnit_Parent_ID) REFERENCES test_BuildingUnit (ID);

ALTER TABLE test_BuildingUnit
    ADD CONSTRAINT test_BuildingUnit_Root_FK FOREIGN KEY (BuildingUnit_Root_ID) REFERENCES test_BuildingUnit (ID);

ALTER TABLE test_BuildingUnit
    ADD CONSTRAINT test_BuildingUni_lod1MultiS_FK FOREIGN KEY (lod1MultiSurface_ID) REFERENCES SURFACE_GEOMETRY (ID);

ALTER TABLE test_BuildingUnit
    ADD CONSTRAINT test_BuildingUni_lod2MultiS_FK FOREIGN KEY (lod2MultiSurface_ID) REFERENCES SURFACE_GEOMETRY (ID);

ALTER TABLE test_BuildingUnit
    ADD CONSTRAINT test_BuildingUni_lod3MultiS_FK FOREIGN KEY (lod3MultiSurface_ID) REFERENCES SURFACE_GEOMETRY (ID);

ALTER TABLE test_BuildingUnit
    ADD CONSTRAINT test_BuildingUni_lod4MultiS_FK FOREIGN KEY (lod4MultiSurface_ID) REFERENCES SURFACE_GEOMETRY (ID);

ALTER TABLE test_BuildingUnit
    ADD CONSTRAINT test_BuildingUnit_lod1Solid_FK FOREIGN KEY (lod1Solid_ID) REFERENCES SURFACE_GEOMETRY (ID);

ALTER TABLE test_BuildingUnit
    ADD CONSTRAINT test_BuildingUnit_lod2Solid_FK FOREIGN KEY (lod2Solid_ID) REFERENCES SURFACE_GEOMETRY (ID);

ALTER TABLE test_BuildingUnit
    ADD CONSTRAINT test_BuildingUnit_lod3Solid_FK FOREIGN KEY (lod3Solid_ID) REFERENCES SURFACE_GEOMETRY (ID);

ALTER TABLE test_BuildingUnit
    ADD CONSTRAINT test_BuildingUnit_lod4Solid_FK FOREIGN KEY (lod4Solid_ID) REFERENCES SURFACE_GEOMETRY (ID);

-- -------------------------------------------------------------------- 
-- test_BuildingUnit_to_address 
-- -------------------------------------------------------------------- 
ALTER TABLE test_BuildingUnit_to_address
    ADD CONSTRAINT test_BuildingUn_to_address_FK1 FOREIGN KEY (BuildingUnit_ID) REFERENCES test_BuildingUnit (ID);

ALTER TABLE test_BuildingUnit_to_address
    ADD CONSTRAINT test_BuildingUn_to_address_FK2 FOREIGN KEY (address_ID) REFERENCES address (ID);

-- -------------------------------------------------------------------- 
-- test_EnergyPerformanceCertific 
-- -------------------------------------------------------------------- 
ALTER TABLE test_EnergyPerformanceCertific
    ADD CONSTRAINT test_EnergyP_Buildin_energy_FK FOREIGN KEY (BuildingUnit_energyPerforma_ID) REFERENCES test_BuildingUnit (ID);

-- -------------------------------------------------------------------- 
-- test_Facilities 
-- -------------------------------------------------------------------- 
ALTER TABLE test_Facilities
    ADD CONSTRAINT test_Facilities_Objectclass_FK FOREIGN KEY (OBJECTCLASS_ID) REFERENCES objectclass (ID);

ALTER TABLE test_Facilities
    ADD CONSTRAINT test_Facilit_Buildin_equipp_FK FOREIGN KEY (BuildingUnit_equippedWith_ID) REFERENCES test_BuildingUnit (ID);

-- -------------------------------------------------------------------- 
-- test_IndustrialBuilding 
-- -------------------------------------------------------------------- 
ALTER TABLE test_IndustrialBuilding
    ADD CONSTRAINT test_IndustrialBuilding_FK FOREIGN KEY (ID) REFERENCES building (ID);

-- -------------------------------------------------------------------- 
-- test_IndustrialBuildingPart 
-- -------------------------------------------------------------------- 
ALTER TABLE test_IndustrialBuildingPart
    ADD CONSTRAINT test_IndustrialBuildingPart_FK FOREIGN KEY (ID) REFERENCES building (ID);

-- -------------------------------------------------------------------- 
-- test_IndustrialBuildingRoofSur 
-- -------------------------------------------------------------------- 
ALTER TABLE test_IndustrialBuildingRoofSur
    ADD CONSTRAINT test_IndustrialBuildingRoof_FK FOREIGN KEY (ID) REFERENCES thematic_surface (ID);

-- -------------------------------------------------------------------- 
-- test_OtherCo_to_themati_surfac 
-- -------------------------------------------------------------------- 
ALTER TABLE test_OtherCo_to_themati_surfac
    ADD CONSTRAINT test_OtherC_to_thema_surfa_FK1 FOREIGN KEY (OtherConstruction_ID) REFERENCES test_OtherConstruction (ID);

ALTER TABLE test_OtherCo_to_themati_surfac
    ADD CONSTRAINT test_OtherC_to_thema_surfa_FK2 FOREIGN KEY (thematic_surface_ID) REFERENCES thematic_surface (ID);

-- -------------------------------------------------------------------- 
-- test_OtherConstruction 
-- -------------------------------------------------------------------- 
ALTER TABLE test_OtherConstruction
    ADD CONSTRAINT test_OtherConstruction_FK FOREIGN KEY (ID) REFERENCES cityobject (ID);

-- -------------------------------------------------------------------- 
-- test_building 
-- -------------------------------------------------------------------- 
ALTER TABLE test_building
    ADD CONSTRAINT test_building_FK FOREIGN KEY (ID) REFERENCES building (ID);

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************  Create Indexes  ************************************* 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- test_BuildingUnit 
-- -------------------------------------------------------------------- 
CREATE INDEX test_BuildingUn_Objectclas_FKX ON test_BuildingUnit
    USING btree
    (
      OBJECTCLASS_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX test_Buildin_buildi_buildi_FKX ON test_BuildingUnit
    USING btree
    (
      building_buildingUnit_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX test_BuildingUnit_Parent_FKX ON test_BuildingUnit
    USING btree
    (
      BuildingUnit_Parent_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX test_BuildingUnit_Root_FKX ON test_BuildingUnit
    USING btree
    (
      BuildingUnit_Root_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX test_BuildingUn_lod1MultiS_FKX ON test_BuildingUnit
    USING btree
    (
      lod1MultiSurface_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX test_BuildingUn_lod2MultiS_FKX ON test_BuildingUnit
    USING btree
    (
      lod2MultiSurface_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX test_BuildingUn_lod3MultiS_FKX ON test_BuildingUnit
    USING btree
    (
      lod3MultiSurface_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX test_BuildingUn_lod4MultiS_FKX ON test_BuildingUnit
    USING btree
    (
      lod4MultiSurface_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX test_BuildingUni_lod1Solid_FKX ON test_BuildingUnit
    USING btree
    (
      lod1Solid_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX test_BuildingUni_lod2Solid_FKX ON test_BuildingUnit
    USING btree
    (
      lod2Solid_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX test_BuildingUni_lod3Solid_FKX ON test_BuildingUnit
    USING btree
    (
      lod3Solid_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX test_BuildingUni_lod4Solid_FKX ON test_BuildingUnit
    USING btree
    (
      lod4Solid_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX test_BuildingUn_lod2MultiC_SPX ON test_BuildingUnit
    USING gist
    (
      lod2MultiCurve
    );

CREATE INDEX test_BuildingUn_lod3MultiC_SPX ON test_BuildingUnit
    USING gist
    (
      lod3MultiCurve
    );

CREATE INDEX test_BuildingUn_lod4MultiC_SPX ON test_BuildingUnit
    USING gist
    (
      lod4MultiCurve
    );

-- -------------------------------------------------------------------- 
-- test_EnergyPerformanceCertific 
-- -------------------------------------------------------------------- 
CREATE INDEX test_EnergyP_Buildi_energy_FKX ON test_EnergyPerformanceCertific
    USING btree
    (
      BuildingUnit_energyPerforma_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- test_Facilities 
-- -------------------------------------------------------------------- 
CREATE INDEX test_Facilities_Objectclas_FKX ON test_Facilities
    USING btree
    (
      OBJECTCLASS_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX test_Facilit_Buildi_equipp_FKX ON test_Facilities
    USING btree
    (
      BuildingUnit_equippedWith_ID ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************  Create Sequences  *********************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 

CREATE SEQUENCE test_EnergyPerformanceCert_SEQ
INCREMENT BY 1
MINVALUE 0
MAXVALUE 2147483647
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;

