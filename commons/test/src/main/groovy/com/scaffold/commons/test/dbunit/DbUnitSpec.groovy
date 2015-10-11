package com.scaffold.commons.test.dbunit

import javax.inject.Inject
import javax.sql.DataSource
 
import org.dbunit.DataSourceDatabaseTester
import org.dbunit.IDatabaseTester
import org.dbunit.operation.DatabaseOperation
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.dbunit.ext.mssql.InsertIdentityOperation
import org.springframework.context.ApplicationContext
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy

import groovy.xml.StreamingMarkupBuilder
import spock.lang.Specification

import java.sql.Timestamp

/**
 * Base class for DbUnit tests
 *
 */
abstract class DbUnitSpec extends Specification {
    
    def static NULL = "[NULL]"

    @Inject
    DataSource dataSource

    @Inject
    ApplicationContext ctx

    IDatabaseTester databaseTester

    def setup() {
        dataSource != null
        ctx != null

        if ( !databaseTester ) {
            databaseTester = new DataSourceDatabaseTester(new TransactionAwareDataSourceProxy(dataSource))
        }
    }

    def cleanup() {
        databaseTester.onTearDown()
	}
	
    /**
     * 
     * Set up DbUnit dataset. By default DBUnit tests should run in a transactional rolledback tests, so tearDownOperation defaults to NONE.
     * If this method is invoked in a transaction-less context this default value can be overridden.
     * @return
     */
    def dataSet = { data, setUpOperation = DatabaseOperation.CLEAN_INSERT, tearDownOperation = DatabaseOperation.NONE ->
		databaseTester.dataSet = dbUnitDataSet data      
        databaseTester.setUpOperation = setUpOperation
        databaseTester.tearDownOperation = tearDownOperation
        databaseTester.onSetup()
		
    }
    
    def dbUnitDataSet = { data ->
        def xmlData = new StreamingMarkupBuilder().bind {dataset data}
        def xmlReader = new StringReader(xmlData.toString())
        def replacementDataSet = new ReplacementDataSet(new FlatXmlDataSetBuilder().build(xmlReader))
        replacementDataSet.addReplacementObject(NULL, null)
		replacementDataSet
    }
}