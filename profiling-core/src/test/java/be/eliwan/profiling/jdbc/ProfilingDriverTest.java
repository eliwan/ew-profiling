/*
 * This file is part of ew-profiling, a library for in-app, runtime profiling.
 * Copyright (c) Eliwan bvba, Belgium, http://eliwan.be
 *
 * The software is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package be.eliwan.profiling.jdbc;

import be.eliwan.profiling.api.GroupData;
import be.eliwan.profiling.service.ProfilingContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verify that profiling driver proxies and profiles.
 */
public class ProfilingDriverTest {

    private Connection connection;
    private ProfilingContainer profilingContainer;

    @Before
    public void getConnection() throws Exception {
        Class.forName("be.eliwan.profiling.jdbc.ProfilingDriver");
        Class.forName("org.hsqldb.jdbcDriver");

        profilingContainer = new ProfilingContainer();
        profilingContainer.start();

        ProfilingDriver.addListener(new ProfilingListener() {
            @Override
            public void register(String group, long durationMillis) {
                profilingContainer.register(group, durationMillis);
            }
            @Override
            public void registerQuery(String group, String query, long durationMillis) {
                profilingContainer.register(group + ":" + query, durationMillis);
            }
        });

        connection = DriverManager.getConnection("profiling:jdbc:hsqldb:mem:testdb", "sa", "");
    }

    @After
    public void closeConnection() throws Exception {
        sql("SHUTDOWN");
        connection.close();
    }

    private ResultSet sql(String sql) throws Exception {
        Statement st = connection.createStatement();
        st.execute(sql);
        return st.getResultSet();
    }


    @Test
    public void testProfilingDriver() throws Exception {
        // test registering connection
        Thread.sleep(100); // give profiling container time to summarize data
        List<GroupData> groupsData = profilingContainer.getGroupData();
        assertThat(groupsData).hasSize(1);
        assertThat(groupsData.get(0).getGroup()).isEqualTo("Driver.connect");
        assertThat(groupsData.get(0).getInvocationCount()).isEqualTo(1);
        profilingContainer.clear();


        // test profiling a statement

        sql("CREATE TABLE bla (\n" +
                "VERSION INTEGER,\n" +
                "NAME VARCHAR(255)\n" +
                ");");
        sql("INSERT INTO bla (NAME, VERSION) values ('zzz', 8)");

        ResultSet resultSet = sql("SELECT * from bla");
        assertThat(resultSet.next()).isTrue();
        assertThat(resultSet.getString("NAME")).isEqualTo("zzz");
        assertThat(resultSet.getInt("VERSION")).isEqualTo(8);

        Thread.sleep(100); // give profiling container time to summarize data
        groupsData = profilingContainer.getGroupData();
        assertThat(groupsData).hasSize(6); // 3 normal, 3 with query
        assertThat(groupsData.toString()).contains(
                "[GroupContainer{group='Connection.createStatement', OneContainer{invocationCount=3");
        assertThat(groupsData.toString()).contains(
                "GroupContainer{group='Statement.execute', OneContainer{invocationCount=3");
        assertThat(groupsData.toString()).contains(
                "GroupContainer{group='Statement.getResultSet', OneContainer{invocationCount=3");
        assertThat(groupsData.toString()).contains(
                "GroupContainer{group='Statement.execute:SELECT * from bla', OneContainer{invocationCount=1");
        profilingContainer.clear();


        // test profiling a prepared statement

        PreparedStatement ps = connection.prepareStatement("SELECT NAME, VERSION as V from bla");
        ps.execute();
        ps.execute();
        resultSet = ps.getResultSet();
        assertThat(resultSet.next()).isTrue();
        assertThat(resultSet.getString("NAME")).isEqualTo("zzz");
        assertThat(resultSet.getInt("V")).isEqualTo(8);

        Thread.sleep(100); // give profiling container time to summarize data
        groupsData = profilingContainer.getGroupData();
        assertThat(groupsData).hasSize(4); // 3 normal, 1 SQL
        assertThat(groupsData.toString()).contains(
                "[GroupContainer{group='Connection.prepareStatement', OneContainer{invocationCount=1");
        assertThat(groupsData.toString()).contains(
                "GroupContainer{group='PreparedStatement.execute', OneContainer{invocationCount=2");
        assertThat(groupsData.toString()).contains(
                "GroupContainer{group='PreparedStatement.getResultSet', OneContainer{invocationCount=1");
        profilingContainer.clear();
    }

}
