package org.citydb.plugins.ade_manager.transformation.sql;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformInfo;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.ForeignKey;
import org.apache.ddlutils.model.Reference;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.platform.SqlBuilder;
import org.apache.ddlutils.platform.oracle.Oracle10Platform;
import org.apache.ddlutils.platform.postgresql.PostgreSqlPlatform;
import org.citydb.log.Logger;
import org.citydb.plugins.ade_manager.config.ConfigImpl;
import org.citydb.plugins.ade_manager.transformation.graph.ADEschemaHelper;
import org.citydb.plugins.ade_manager.transformation.graph.GraphNodeArcType;

import agg.attribute.AttrInstance;
import agg.xt_basis.Arc;
import agg.xt_basis.GraGra;
import agg.xt_basis.Node;
import agg.xt_basis.Type;

public class DBScriptGenerator {	
	private Map<String, Table> databaseTables;
	private GraGra graphGrammar;
	
	private List<String> dbFkConstratintNameList;
	private List<String> dbIndexNameList;
	private Map<String, List<String>> dbTableColumnsMap;
	private Platform databasePlatform;
	private ConfigImpl config;
	private static final String indentStr = "    ";
	
	private static final String postgresSQL_FolderName = "postgreSQL";
	private static final String oracle_FolderName = "oracle";
	private static final String create_ADE_DB_fileName = "CREATE_ADE_DB.sql";
	private static final String drop_ADE_DB_fileName = "DROP_ADE_DB.sql";
	private static final String enable_ADE_versioning_fileName = "ENABLE_ADE_VERSIONING.sql";
	private static final String disable_ADE_versioning_fileName = "DISABLE_ADE_VERSIONING.sql";
	
	private final Logger LOG = Logger.getInstance();
	
	
	public DBScriptGenerator(GraGra graphGrammar, ConfigImpl config) {
		this.graphGrammar = graphGrammar;		
		new ArrayList<String>(); 
		this.dbFkConstratintNameList = new ArrayList<String>(); 
		this.dbIndexNameList = new ArrayList<String>(); 
		this.dbTableColumnsMap = new HashMap<String, List<String>>();
		this.databaseTables = new HashMap<String, Table>();	
		this.config = config;
	}

	public void createDatabaseScripts() {
		// shorten database object name;
		this.shrotenDatabaseObjectName();

		// create database tables
		List<Node> tableNodes = getTableNodes();
		Iterator<Node> iter = tableNodes.iterator();
		while (iter.hasNext()) {
			Node tableNode = iter.next();
			this.createDatabaseTable(tableNode);			
		}
		
		// create foreign key constraints
		List<Node> joinNodes = getJoinNodes();
		iter = joinNodes.iterator();
		while (iter.hasNext()) {
			Node joinNode = iter.next();
			this.createForeignKeyContraint(joinNode);			
		}
		
		Database database = new Database();	
		this.addDefaul3DCityDBTablesToDatabase(database);
		List<Table> list = new ArrayList<Table>(databaseTables.values());
		database.addTables(list);
		
		// Oracle version
		databasePlatform = new Oracle10Platform();		
		this.marshallingDatabaseSchema(databasePlatform, database);
		
		// PgSQL version
		databasePlatform = new PostgreSqlPlatform();		
		this.marshallingDatabaseSchema(databasePlatform, database);
	}
	
	public Platform getDatabasePlatform () {
		return this.databasePlatform;
	}
	
	private void createDatabaseTable(Node tableNode) {
		String tableName = (String) tableNode.getAttribute().getValueAt("name");	
		Table dbTable = new Table();
		dbTable.setName(tableName);
				
		Iterator<Arc> iter = tableNode.getIncomingArcs();
		while (iter.hasNext()) {
			Arc arc = iter.next();
			if (arc.getType().getName().equalsIgnoreCase(GraphNodeArcType.BelongsTo)) {
				Node columnNode = (Node) arc.getSource();
				if (columnNode.getType().getParent().getName().equalsIgnoreCase(GraphNodeArcType.JoinColumn)
						|| columnNode.getType().getName().equalsIgnoreCase(GraphNodeArcType.JoinColumn)) {
					this.createJoinColumn(dbTable, columnNode);
				}
				else if (columnNode.getType().getName().equalsIgnoreCase(GraphNodeArcType.NormalDataColumn)) {
					this.createNoramlDataColumn(dbTable, columnNode);
				}
				else if (columnNode.getType().getName().equalsIgnoreCase(GraphNodeArcType.GenericDataColumn)) {
					this.createGeneircDataColumn(dbTable, columnNode);
				}
				else if (columnNode.getType().getName().equalsIgnoreCase(GraphNodeArcType.InlineGeometryColumn)) {
					this.createInlineGeometryColumn(dbTable, columnNode);
				}
			}
		}
		
		databaseTables.put(tableName, dbTable);
	}
	
	private void createIndexForColumn(Table dbTable, IndexedColumn indexedColumn, Node columnNode) {
		Iterator<Arc> iter = columnNode.getIncomingArcs();
		while (iter.hasNext()) {
			Arc arc = iter.next();
			if (arc.getType().getName().equalsIgnoreCase(GraphNodeArcType.TargetColumn)) {
				Node sourceNode = (Node) arc.getSource();
				if (sourceNode.getType().getName().equalsIgnoreCase(GraphNodeArcType.Index)) {
					String indexName = (String) sourceNode.getAttribute().getValueAt("name");
					indexedColumn.setIndexName(indexName);
				}
			}			
		}
	}
	
	private void createGeneircDataColumn(Table dbTable, Node columnNode) {
		String columnName = (String) columnNode.getAttribute().getValueAt("name");
		Column column = new Column();
		column.setName(columnName);
		column.setTypeCode(Types.CLOB);
		dbTable.addColumn(column);
	}
		
	private void createJoinColumn(Table dbTable, Node columnNode) {
		String columnName = (String) columnNode.getAttribute().getValueAt("name");		
		if (columnNode.getType().getName().equalsIgnoreCase(GraphNodeArcType.PrimaryKeyColumn)) {
			Column column = new Column();
			column.setName(columnName);
			column.setTypeCode(Types.INTEGER);
			column.setPrimaryKey(true);
			column.setRequired(true);
			dbTable.addColumn(column);
		}
		else {
			IndexedColumn indexedColumn = new IndexedColumn();
			indexedColumn.setName(columnName);
			indexedColumn.setTypeCode(Types.INTEGER);
			this.createIndexForColumn(dbTable, indexedColumn, columnNode);	
			dbTable.addColumn(indexedColumn);
		}		
	}
	
	private void createNoramlDataColumn(Table dbTable, Node columnNode) {
		String columnName = (String)columnNode.getAttribute().getValueAt("name");
		String columnSourceType = (String)columnNode.getAttribute().getValueAt("primitiveDataType");
		Column column = new Column();
		column.setName(columnName);

		if (columnSourceType.equalsIgnoreCase("string")) {
			column.setTypeCode(Types.VARCHAR);
			column.setSize("1000");
		}
		else if (columnSourceType.equalsIgnoreCase("boolean")) {
			column.setTypeCode(Types.BOOLEAN);
		}
		else if (columnSourceType.equalsIgnoreCase("double")) {
			column.setTypeCode(Types.NUMERIC);
		}
		else if (columnSourceType.equalsIgnoreCase("date")) {
			column.setTypeCode(Types.DATE);
		}
		else if (columnSourceType.equalsIgnoreCase("timestamp")) {
			column.setTypeCode(Types.TIMESTAMP);
		}
		else if (columnSourceType.equalsIgnoreCase("integer")) {
			column.setTypeCode(Types.INTEGER);
		}
		else {
			LOG.error("Incorrect Data Type at column: " + column.getName());
			column.setTypeCode(Types.CLOB);
		}
		
		dbTable.addColumn(column);
	}
	
	private void createInlineGeometryColumn (Table dbTable, Node columnNode) {
		String columnName = (String) columnNode.getAttribute().getValueAt("name");
		SpatialColumn column = new SpatialColumn(this);
		column.setName(columnName);
		dbTable.addColumn(column);
		this.createIndexForColumn(dbTable, column, columnNode);	
	}
	
	private void createForeignKeyContraint(Node joinNode) {
		Iterator<Arc> iter = joinNode.getOutgoingArcs();
		
		String joinFromColumnName = null;
		String joinFromTableName = null;
		String joinToColumnName = null;
		String joinToTableName = null;
		Node joinFromColumnNode = null;
		Node joinToColumnNode = null;
		while (iter.hasNext()) {
			Arc arc = iter.next();
			if (arc.getType().getName().equalsIgnoreCase(GraphNodeArcType.JoinFrom)) {
				joinFromColumnNode = (Node) arc.getTarget();
				Node joinFromTableNode = (Node)joinFromColumnNode.getOutgoingArcs().next().getTarget();
				joinFromColumnName = (String)joinFromColumnNode.getAttribute().getValueAt("name");
				joinFromTableName= (String)joinFromTableNode.getAttribute().getValueAt("name");
			}
			else if (arc.getType().getName().equalsIgnoreCase(GraphNodeArcType.JoinTo)) {
				joinToColumnNode = (Node) arc.getTarget();
				Node joinToTableNode = (Node)joinToColumnNode.getOutgoingArcs().next().getTarget();
				joinToColumnName = (String)joinToColumnNode.getAttribute().getValueAt("name");
				joinToTableName= (String)joinToTableNode.getAttribute().getValueAt("name");
			}
		}
		
		String fkName = (String) joinNode.getAttribute().getValueAt("name");
		ForeignKey fk = new ForeignKey();
		fk.setName(fkName);
		fk.setForeignTableName(joinToTableName);	
		Reference refer = new Reference();
		refer.setLocalColumnName(joinFromColumnName);
		refer.setForeignColumnName(joinToColumnName);
		fk.addReference(refer);		
		Table localTable = databaseTables.get(joinFromTableName);
		localTable.addForeignKey(fk);
	}
	
	private void addDefaul3DCityDBTablesToDatabase(Database database) {
		Table surfaceGeometryTable = new Table();
		Column pkColumn = new Column();
		pkColumn.setName("ID");
		pkColumn.setTypeCode(Types.NUMERIC);
		surfaceGeometryTable.addColumn(pkColumn);
		surfaceGeometryTable.setName("SURFACE_GEOMETRY");
		database.addTable(surfaceGeometryTable);
		
		Table implicitGeometryTable = new Table();
		pkColumn = new Column();
		pkColumn.setName("ID");
		pkColumn.setTypeCode(Types.NUMERIC);
		implicitGeometryTable.addColumn(pkColumn);
		implicitGeometryTable.setName("IMPLICIT_GEOMETRY");
		database.addTable(implicitGeometryTable);
	}
	
	private void printComment(String text, Platform platform, PrintWriter writer) throws IOException
    {
        if (platform.isSqlCommentsOn())
        {
        	PlatformInfo platformInfo = platform.getPlatformInfo();
        	writer.print(platformInfo.getCommentPrefix());
        	writer.print(" ");
        	writer.print(text);
        	writer.print(" ");
        	writer.print(platformInfo.getCommentSuffix());
        	writer.println();
        }
    }
	
	private void shrotenDatabaseObjectName() {
		int maxTableNameLength = 25;
		int maxColumnNameLength = 28;
		int maxIndexNameLength = 26;
		int maxConstraintNameLength = 26;
		int maxSequenceNameLength = 25;
		
		String prefix = config.getAdeDbPrefix();
		
		if (prefix.length() > 4)
			prefix = prefix.substring(0, 4);		
		int prefixLength = prefix.length();
		
		int maxTableNameLengthWithPrefix = maxTableNameLength - prefixLength - 1;
		int maxIndexNameLengthWithPrefix = maxIndexNameLength - prefixLength - 1;
		int maxConstraintNameLengthWithPrefix = maxConstraintNameLength - prefixLength - 1;
		int maxSequenceNameLengthWithPrefix = maxSequenceNameLength - prefixLength - 1;
		
		Enumeration<Type> e = this.graphGrammar.getTypes();
		while(e.hasMoreElements()){
			Type nodeType = e.nextElement();
			if (nodeType.getName().equalsIgnoreCase(GraphNodeArcType.DatabaseObject)) {
				List<Node> nodes = this.graphGrammar.getGraph().getNodes(nodeType);
				Iterator<Node> iter = nodes.iterator();
				while (iter.hasNext()) {
					Node databaseObjectNode = iter.next();					
					AttrInstance attr = databaseObjectNode.getAttribute();										
					String nodeTypeName = databaseObjectNode.getType().getName();
					String originalDatabaseObjectName = (String) attr.getValueAt("name");
					String shortenedName = null;
					if (nodeTypeName.equalsIgnoreCase(GraphNodeArcType.DataTable) || nodeTypeName.equalsIgnoreCase(GraphNodeArcType.JoinTable)) {												
						if (!ADEschemaHelper.CityDB_Tables.containsValue(originalDatabaseObjectName)) {
							shortenedName = NameShortener.shortenDbObjectName(originalDatabaseObjectName, maxTableNameLengthWithPrefix);
							shortenedName = prefix + "_" + shortenedName;							
							Iterator<Arc> iter2 = databaseObjectNode.getIncomingArcs();
							while (iter2.hasNext()) {
								Arc arc = iter2.next();
								if (arc.getType().getName().equalsIgnoreCase(GraphNodeArcType.BelongsTo)) {
									Node columnNode = (Node) arc.getSource();
									String columnName = (String)columnNode.getAttribute().getValueAt("name");
									columnName = NameShortener.shortenDbObjectName(columnName, maxColumnNameLength);	
									String processedColumnName = this.processDuplicatedDbColumnName(shortenedName, columnName, maxColumnNameLength, 0);
									columnNode.getAttribute().setValueAt(processedColumnName, "name");								
								}
							}	
						}											
					}
					else if (nodeTypeName.equalsIgnoreCase(GraphNodeArcType.Join)) {
						shortenedName = NameShortener.shortenDbObjectName(originalDatabaseObjectName, maxConstraintNameLengthWithPrefix);
						shortenedName = prefix + "_" + shortenedName;		
						shortenedName = this.processDuplicatedDbConstraintName(shortenedName, maxConstraintNameLength, 0);
					}	
					else if (nodeTypeName.equalsIgnoreCase(GraphNodeArcType.Index)) {
						shortenedName = NameShortener.shortenDbObjectName(originalDatabaseObjectName, maxIndexNameLengthWithPrefix);
						shortenedName = prefix + "_" + shortenedName;		
						shortenedName = this.processDuplicatedDbIndexName(shortenedName, maxIndexNameLength, 0);
					}
					else if (nodeTypeName.equalsIgnoreCase(GraphNodeArcType.Sequence)) {
						shortenedName = NameShortener.shortenDbObjectName(originalDatabaseObjectName, maxSequenceNameLengthWithPrefix);
						shortenedName = prefix + "_" + shortenedName;		
					}
					
					if (shortenedName != null) {
						attr.setValueAt(shortenedName, "name");
					}									
				}		
				break;
			};
		}
	}
	
	private String processDuplicatedDbConstraintName(String inputString, int maxLength, int k) {
		if (!dbFkConstratintNameList.contains(inputString)) {
			dbFkConstratintNameList.add(inputString);		
			return inputString;
		}
		else {
			k++;
			inputString = NameShortener.shortenDbObjectName(inputString, maxLength, k);	
			return processDuplicatedDbConstraintName(inputString, maxLength, k);
		}
	}
	
	private String processDuplicatedDbIndexName(String inputString, int maxLength, int k) {
		if (!dbIndexNameList.contains(inputString)) {
			dbIndexNameList.add(inputString);		
			return inputString;
		}
		else {
			k++;
			inputString = NameShortener.shortenDbObjectName(inputString, maxLength, k);	
			return processDuplicatedDbIndexName(inputString, maxLength, k);
		}
	}
	
	private String processDuplicatedDbColumnName(String tableName, String inputString, int maxLength, int k) {
		if (!dbTableColumnsMap.containsKey(tableName)) {
			dbTableColumnsMap.put(tableName, new ArrayList<String>());
		}
		
		List<String> columnList = dbTableColumnsMap.get(tableName);
		if (!columnList.contains(inputString)) {
			columnList.add(inputString);		
			return inputString;
		}
		else {
			k++;
			inputString = NameShortener.shortenDbObjectName(inputString, maxLength, k);	
			return processDuplicatedDbColumnName(tableName, inputString, maxLength, k);
		}
	}
	
	private List<Node> getTableNodes() {
		Enumeration<Type> e = this.graphGrammar.getTypes();
		while(e.hasMoreElements()){
			Type nodeType = e.nextElement();
			if (nodeType.getName().equalsIgnoreCase(GraphNodeArcType.Table)) {
				List<Node> nodes = this.graphGrammar.getGraph().getNodes(nodeType);
				return nodes;
			};
		}
		return null;
	}
	
	private List<Node> getJoinNodes() {
		Enumeration<Type> e = this.graphGrammar.getTypes();
		while(e.hasMoreElements()){
			Type nodeType = e.nextElement();
			if (nodeType.getName().equalsIgnoreCase(GraphNodeArcType.Join)) {
				List<Node> nodes = this.graphGrammar.getGraph().getNodes(nodeType);
				return nodes;
			};
		}
		return null;
	}
	
	private Node getTableNodeByName(String tName) {
		List<Node> tableNodeList =  this.getTableNodes();
		Iterator<Node> iter = tableNodeList.iterator();
		while (iter.hasNext()) {
			Node tableNode = iter.next();
			String tableName = (String) tableNode.getAttribute().getValueAt("name");
			if (tableName.equalsIgnoreCase(tName))
				return tableNode;
		}
		return null;
	}
	
	private boolean isMappedFromforeignClass(String tableName) {
		if (tableName.equalsIgnoreCase("Objectclass") || tableName.equalsIgnoreCase("surface_geometry") || tableName.equalsIgnoreCase("implicit_geometry"))
			return true;
		
		Node tableNode = this.getTableNodeByName(tableName);
		Iterator<Arc> iter = tableNode.getIncomingArcs();
		while (iter.hasNext()) {
			Arc arc = iter.next();
			Node sourceNode = (Node) arc.getSource();
			String sourceNodeTypeName = sourceNode.getType().getName();
			if (sourceNodeTypeName.equalsIgnoreCase(GraphNodeArcType.ComplexType)) {
				if ((boolean) sourceNode.getAttribute().getValueAt("isForeign")) {					
					return true;
				}
				else {					
					return false;
				}
			}
		}
		return false;
	}

	private void marshallingDatabaseSchema (Platform databasePlatform, Database database) {
		String headerText = "This document was automatically created by the ADE-Manager tool of 3DCityDB (https://www.3dcitydb.org) on " +  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String outputFolderPath = config.getTransformationOutputPath();
		String dbFolderName = null;
		
		if (databasePlatform instanceof Oracle10Platform) {
			dbFolderName = oracle_FolderName;
		}
		else if (databasePlatform instanceof PostgreSqlPlatform) {
			dbFolderName = postgresSQL_FolderName;
		}

		File directory = new File(outputFolderPath, dbFolderName);
		if (!directory.exists()) 
			directory.mkdir();
		else
			directory.delete();
		
		File createDbFile = new File(directory, create_ADE_DB_fileName);
		File dropDbFile = new File(directory, drop_ADE_DB_fileName);
		File enableVersioningFile = new File(directory, enable_ADE_versioning_fileName);
		File disableVersioningFile = new File(directory, disable_ADE_versioning_fileName);
		
		Map<String, Table> treeMap = new TreeMap<String, Table>(databaseTables);
		int counter = 0;	
		
		// Create Database Schema for ADE...
		PrintWriter writer = null;
		try {
			// create tables...
			writer = new PrintWriter(createDbFile);
			SqlBuilder sqlBuilder = new SqlBuilder(databasePlatform) {};		
			sqlBuilder.setWriter(writer);
			printComment(headerText, databasePlatform, writer);	
			printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);						
			printComment("***********************************  Create tables *************************************", databasePlatform, writer);
			printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);				
			Iterator<Table> iterator = treeMap.values().iterator();			
			while (iterator.hasNext()) {
				Table table = iterator.next();
				if (!isMappedFromforeignClass(table.getName())) {
					printComment("--------------------------------------------------------------------", databasePlatform, writer);						
					printComment(table.getName(), databasePlatform, writer);
					printComment("--------------------------------------------------------------------", databasePlatform, writer);
					sqlBuilder.createTable(database, table);
					counter++;	
				}
			}
			LOG.info(counter + " Tables have been generated for " + dbFolderName);

			// create foreign key constraints
			printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);						
			printComment("*********************************  Create foreign keys  ********************************", databasePlatform, writer);
			printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);	
			iterator = treeMap.values().iterator();
			while (iterator.hasNext()) {
				Table table = iterator.next();
				if (!isMappedFromforeignClass(table.getName()) && table.getForeignKeyCount() > 0) {
					printComment("--------------------------------------------------------------------", databasePlatform, writer);						
					printComment(table.getName(), databasePlatform, writer);
					printComment("--------------------------------------------------------------------", databasePlatform, writer);
					sqlBuilder.createExternalForeignKeys(database, table);	
				}
			}
			
			// create Indexes 
			printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);						
			printComment("*********************************  Create Indexes  *************************************", databasePlatform, writer);
			printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);	
			
			if (databasePlatform instanceof Oracle10Platform && checkExistenceOfNodeOrArc(GraphNodeArcType.InlineGeometryColumn))
				this.printGetSridScript(writer);
			
			iterator = treeMap.values().iterator();
			while (iterator.hasNext()) {
				Table table = iterator.next();
				if (!isMappedFromforeignClass(table.getName())) {
					this.printIndexes(table, writer);
				}
			}		
			
			// create sequences
			printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);						
			printComment("*********************************  Create Sequences  ***********************************", databasePlatform, writer);
			printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);	
			this.printCreateSequences(writer);	

		} catch (IOException | NullPointerException e) {			
			e.printStackTrace();
		} finally {
			writer.close();	
		}

		// Drop Database Schema for ADE...
		try {
			// drop foreign key constraints
			writer = new PrintWriter(dropDbFile);
			SqlBuilder sqlBuilder = new SqlBuilder(databasePlatform) {};		
			sqlBuilder.setWriter(writer);
			printComment(headerText, databasePlatform, writer);	
			printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);						
			printComment("***********************************  Drop foreign keys *********************************", databasePlatform, writer);
			printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);		
			Iterator<Table> iterator = treeMap.values().iterator();
			while (iterator.hasNext()) {
				Table table = iterator.next();
				if (!isMappedFromforeignClass(table.getName()) && table.getForeignKeyCount() > 0) {
					printComment("--------------------------------------------------------------------", databasePlatform, writer);						
					printComment(table.getName(), databasePlatform, writer);
					printComment("--------------------------------------------------------------------", databasePlatform, writer);
					sqlBuilder.dropExternalForeignKeys(table);
				}
			}
			
			// drop tables
			printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);						
			printComment("***********************************  Drop tables  **************************************", databasePlatform, writer);
			printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);	
			iterator = treeMap.values().iterator();
			while (iterator.hasNext()) {
				Table table = iterator.next();
				if (!isMappedFromforeignClass(table.getName())) {
					printComment("--------------------------------------------------------------------", databasePlatform, writer);						
					printComment(table.getName(), databasePlatform, writer);
					printComment("--------------------------------------------------------------------", databasePlatform, writer);
					sqlBuilder.dropTable(table);
				}
			}
			
			// drop sequences
			printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);						
			printComment("*********************************  Drop Sequences  *************************************", databasePlatform, writer);
			printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);	
			this.printDropSequences(writer);

			// Recycle Bin for Oracle
			if (databasePlatform instanceof Oracle10Platform)
				printRecycleBinForOracle(writer);
		} catch (IOException | NullPointerException e) {			
			e.printStackTrace();
		} finally {
			// Close Writer instance
			writer.close();
		}
		
		// enable Versioning
		if (databasePlatform instanceof Oracle10Platform){
			try {
				writer = new PrintWriter(enableVersioningFile);
				printComment(headerText, databasePlatform, writer);	
				printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);						
				printComment("*********************************  Enable Versioning  ***********************************", databasePlatform, writer);
				printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);	
				writer.println();
				Iterator<Table> iterator = treeMap.values().iterator();
				StringBuilder commandStr = new StringBuilder().append("DBMS_WM.EnableVersioning('"); 
				while (iterator.hasNext()) {
					Table table = iterator.next();
					String tableName = table.getName();
					if (!isMappedFromforeignClass(tableName)) 
						commandStr.append(tableName).append(iterator.hasNext()?",":"");										
				}	
				commandStr.append("','VIEW_WO_OVERWRITE');"); 

				writer.println("exec " + commandStr.toString());
			} catch (IOException | NullPointerException e) {			
				e.printStackTrace();
			} finally {
				writer.close();	
			}
		}  	
		
		// disable Versioning
		if (databasePlatform instanceof Oracle10Platform){
			try {
				writer = new PrintWriter(disableVersioningFile);
				printComment(headerText, databasePlatform, writer);	
				printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);						
				printComment("*********************************  Disable Versioning  ***********************************", databasePlatform, writer);
				printComment("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", databasePlatform, writer);	
				writer.println();
				Iterator<Table> iterator = treeMap.values().iterator();
				StringBuilder commandStr = new StringBuilder().append("DBMS_WM.DisableVersioning('"); 
				while (iterator.hasNext()) {
					Table table = iterator.next();
					String tableName = table.getName();
					if (!isMappedFromforeignClass(tableName)) 
						commandStr.append(tableName).append(iterator.hasNext()?",":"");										
				}	
				commandStr.append("',true, true);"); 

				writer.println("exec " + commandStr.toString());
			} catch (IOException | NullPointerException e) {			
				e.printStackTrace();
			} finally {
				writer.close();	
			}
		}  
	}
	
	private void printGetSridScript(PrintWriter writer) {
		writer.println();
		writer.println("SET SERVEROUTPUT ON");
		writer.println("SET FEEDBACK ON");
		writer.println("SET VER OFF");
/*		writer.println();
		writer.println("ALTER SESSION set NLS_TERRITORY='AMERICA';");
		writer.println("ALTER SESSION set NLS_LANGUAGE='AMERICAN';");
		writer.println();*/
		writer.println("VARIABLE SRID NUMBER;");		
		writer.print("BEGIN");
		writer.println();
		writer.print("  SELECT SRID INTO :SRID FROM DATABASE_SRS;");
		writer.println();
		writer.println("END;");
		writer.println("/");
		writer.println();
		writer.println("column mc new_value SRSNO print");
		writer.println("select :SRID mc from dual;");
		writer.println();
		writer.println("prompt Used SRID for spatial indexes: &SRSNO ");
		writer.println();
	}
	
	private void printIndexes(Table table, PrintWriter writer) throws IOException {
		String tablenName = table.getName();
		boolean flag = false;
		for (int idx = 0; idx < table.getColumnCount(); idx++)
        {
            Column column = table.getColumn(idx);
            if (column instanceof IndexedColumn) {
            	if (!flag) {
            		printComment("--------------------------------------------------------------------", databasePlatform, writer);						
					printComment(table.getName(), databasePlatform, writer);
					printComment("--------------------------------------------------------------------", databasePlatform, writer);
					flag = true;
            	}
            	String indexName = ((IndexedColumn) column).getIndexName();
            	String columnName = column.getName();
            	if (column instanceof SpatialColumn) {
            		if (databasePlatform instanceof PostgreSqlPlatform) {
            			 writer.print("CREATE");
                         writer.print(" INDEX ");
                         writer.print(indexName);
                         writer.print(" ON ");
                         writer.print(tablenName);
	                   	 writer.println();
	                   	 writer.print(indentStr);
	                   	 writer.print("USING gist");
	                   	 writer.println();
	                   	 writer.print(indentStr);
	                   	 writer.print("(");
	                   	 writer.println();
	                   	 writer.print(indentStr + "  ");
	                   	 writer.print(columnName);
	                   	 writer.println();
	                   	 writer.print(indentStr);
	                   	 writer.print(");");
	                   	 writer.println();
                   }
                   else {
                	   writer.print("DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='");
                	   writer.print(tablenName.toUpperCase());
                	   writer.print("' AND COLUMN_NAME='");
                	   writer.print(columnName.toUpperCase());
                	   writer.print("';");
                	   writer.println();
                	   writer.print("INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)");
                	   writer.println();
                	   writer.print("VALUES ('");
                	   writer.print(tablenName);
                	   writer.print("','");
                	   writer.print(columnName);
                	   writer.print("',");
                	   writer.println();
                	   writer.print("MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', 0.000, 10000000.000, 0.0005), MDSYS.SDO_DIM_ELEMENT('Y', 0.000, 10000000.000, 0.0005),MDSYS.SDO_DIM_ELEMENT('Z', -1000, 10000, 0.0005)), &SRSNO);");
                	   writer.println();
                	   writer.print("CREATE");
                       writer.print(" INDEX ");
                       writer.print(indexName);
                       writer.print(" ON ");
                       writer.print(tablenName);
                       writer.print(" (");
                       writer.print(columnName);
                       writer.print(")");    
                       writer.print(" INDEXTYPE IS MDSYS.SPATIAL_INDEX;");
                       writer.println();
                   }
                }
            	else {            		
                    writer.print("CREATE");
                    writer.print(" INDEX ");
                    writer.print(indexName);
                    writer.print(" ON ");
                    writer.print(tablenName);
                    if (databasePlatform instanceof PostgreSqlPlatform) {
                    	 writer.println();
                    	 writer.print(indentStr);
                    	 writer.print("USING btree");
                    	 writer.println();
                    	 writer.print(indentStr);
                    	 writer.print("(");
                    	 writer.println();
                    	 writer.print(indentStr + "  ");
                    	 writer.print(columnName);
                    	 writer.print(" ASC NULLS LAST");
                    	 writer.println();
                    	 writer.print(indentStr);
                    	 writer.print(")");
                    	 writer.print("   WITH (FILLFACTOR = 90);");
                    	 writer.println();
                    }
                    else {
                    	writer.print(" (");
                        writer.print(columnName);
                        writer.print(");");
                        writer.println();                   
                    }                 
            	} 
            	writer.println();
            }          
        }
	}
	
	private void printCreateSequences(PrintWriter writer) {
		Enumeration<Type> e = this.graphGrammar.getTypes();
		while(e.hasMoreElements()){
			Type nodeType = e.nextElement();
			if (nodeType.getName().equalsIgnoreCase(GraphNodeArcType.Sequence)) {
				List<Node> nodes = this.graphGrammar.getGraph().getNodes(nodeType);
				if (nodes == null)
					return;
				Iterator<Node> iter = nodes.iterator();
				while (iter.hasNext()) {
					Node sequenceNode = iter.next();
					String squenceName = (String) sequenceNode.getAttribute().getValueAt("name");
					writer.println();
					writer.print("CREATE SEQUENCE ");
					writer.print(squenceName);
					if (databasePlatform instanceof PostgreSqlPlatform) {	
						writer.println();
						writer.println("INCREMENT BY 1");
						writer.println("MINVALUE 0");
						writer.println("MAXVALUE 2147483647");
						writer.println("START WITH 1");
						writer.println("CACHE 1");
						writer.println("NO CYCLE");
						writer.println("OWNED BY NONE;");						
					}
                   else {
						writer.print(" INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;");
                   }   
					writer.println();
				}
			};
		}
		writer.println();
	}
	
	private void printDropSequences(PrintWriter writer) {
		Enumeration<Type> e = this.graphGrammar.getTypes();
		while(e.hasMoreElements()){
			Type nodeType = e.nextElement();
			if (nodeType.getName().equalsIgnoreCase(GraphNodeArcType.Sequence)) {
				List<Node> nodes = this.graphGrammar.getGraph().getNodes(nodeType);
				if (nodes == null)
					return;
				Iterator<Node> iter = nodes.iterator();
				while (iter.hasNext()) {
					Node sequenceNode = iter.next();
					String squenceName = (String) sequenceNode.getAttribute().getValueAt("name");
					writer.println();
					writer.print("DROP SEQUENCE ");
					writer.print(squenceName + ";");
					writer.println();
				}
			};
		}
	}
	
	private void printRecycleBinForOracle(PrintWriter writer) {
		writer.println();
		writer.print("PURGE RECYCLEBIN;");
		writer.println();
	}
	
	private boolean checkExistenceOfNodeOrArc(String nodeOrArcTypeName) {
		Enumeration<Type> e = this.graphGrammar.getTypes();
		while(e.hasMoreElements()){
			Type nodeType = e.nextElement();
			if (nodeType.getName().equalsIgnoreCase(nodeOrArcTypeName)) {
				List<Node> nodes = this.graphGrammar.getGraph().getNodes(nodeType);
				if (nodes == null)
					return false;
				if (nodes.size() > 0)
					return true;
			};
		}
		return false;
	}

}
