-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- ***********************************  Drop foreign keys ********************************* 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- test_BuildingUnit 
-- -------------------------------------------------------------------- 
ALTER TABLE test_BuildingUnit
    DROP CONSTRAINT test_BuildingUnit_FK;

ALTER TABLE test_BuildingUnit
    DROP CONSTRAINT test_BuildingUnit_Parent_FK;

ALTER TABLE test_BuildingUnit
    DROP CONSTRAINT test_BuildingUnit_Root_FK;

ALTER TABLE test_BuildingUnit
    DROP CONSTRAINT test_Buildin_buildin_buildi_FK;

ALTER TABLE test_BuildingUnit
    DROP CONSTRAINT test_BuildingUni_lod1MultiS_FK;

ALTER TABLE test_BuildingUnit
    DROP CONSTRAINT test_BuildingUni_lod2MultiS_FK;

ALTER TABLE test_BuildingUnit
    DROP CONSTRAINT test_BuildingUni_lod3MultiS_FK;

ALTER TABLE test_BuildingUnit
    DROP CONSTRAINT test_BuildingUni_lod4MultiS_FK;

ALTER TABLE test_BuildingUnit
    DROP CONSTRAINT test_BuildingUnit_lod1Solid_FK;

ALTER TABLE test_BuildingUnit
    DROP CONSTRAINT test_BuildingUnit_lod2Solid_FK;

ALTER TABLE test_BuildingUnit
    DROP CONSTRAINT test_BuildingUnit_lod3Solid_FK;

ALTER TABLE test_BuildingUnit
    DROP CONSTRAINT test_BuildingUnit_lod4Solid_FK;

-- -------------------------------------------------------------------- 
-- test_BuildingUnit_to_address 
-- -------------------------------------------------------------------- 
ALTER TABLE test_BuildingUnit_to_address
    DROP CONSTRAINT test_BuildingUn_to_address_FK1;

ALTER TABLE test_BuildingUnit_to_address
    DROP CONSTRAINT test_BuildingUn_to_address_FK2;

-- -------------------------------------------------------------------- 
-- test_EnergyPerformanceCertific 
-- -------------------------------------------------------------------- 
ALTER TABLE test_EnergyPerformanceCertific
    DROP CONSTRAINT test_EnergyP_Buildin_energy_FK;

-- -------------------------------------------------------------------- 
-- test_Facilities 
-- -------------------------------------------------------------------- 
ALTER TABLE test_Facilities
    DROP CONSTRAINT test_Facilit_Buildin_equipp_FK;

-- -------------------------------------------------------------------- 
-- test_IndustrialBuilding 
-- -------------------------------------------------------------------- 
ALTER TABLE test_IndustrialBuilding
    DROP CONSTRAINT test_IndustrialBuilding_FK;

-- -------------------------------------------------------------------- 
-- test_building 
-- -------------------------------------------------------------------- 
ALTER TABLE test_building
    DROP CONSTRAINT test_building_FK;

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- ***********************************  Drop tables  ************************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- test_BuildingUnit 
-- -------------------------------------------------------------------- 
DROP TABLE test_BuildingUnit;

-- -------------------------------------------------------------------- 
-- test_BuildingUnit_to_address 
-- -------------------------------------------------------------------- 
DROP TABLE test_BuildingUnit_to_address;

-- -------------------------------------------------------------------- 
-- test_EnergyPerformanceCertific 
-- -------------------------------------------------------------------- 
DROP TABLE test_EnergyPerformanceCertific;

-- -------------------------------------------------------------------- 
-- test_Facilities 
-- -------------------------------------------------------------------- 
DROP TABLE test_Facilities;

-- -------------------------------------------------------------------- 
-- test_IndustrialBuilding 
-- -------------------------------------------------------------------- 
DROP TABLE test_IndustrialBuilding;

-- -------------------------------------------------------------------- 
-- test_building 
-- -------------------------------------------------------------------- 
DROP TABLE test_building;

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************  Drop Sequences  ************************************* 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 

DROP SEQUENCE test_EnergyPerformanceCert_SEQ;
