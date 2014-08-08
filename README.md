ew-profiling
============

The ew-profiling project provides a some hooks which allow you to log
time spent (and invocation count) in pieces of code. It is especially built
to be easy to integrate and have a limited impact on the actual execution
time (thanks to the LMAX Disruptor).

The profiling information can be split up into groups.

There is JMX support to allow getting the counter information and
resetting through JMX (for example using JConsole).

There is a profiling JDBC driver which can be used to profile JDBC
access.


[Main site with documentation](http://www/eliwan.be/oss/ew-profiling/)

[javadoc](http://joachimvda.github.io/ew-profiling/)


Configuration
-------------

### Dependencies

Make sure your include the plug-in in your project. If you are using
Maven, add the following dependency to your pom:

    <dependency>
        <groupId>be.eliwan</groupId>
        <artifactId>ew-profiling-core</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>


### Base profiling

For starters, you need to define a profiling container. This is done
in your Spring configuration file. A container allows your t read, write
and reset counters. You can register data in groups and also get the total
for all groups in the container. It is normal to have more than one
profiling container in your application.

    <bean name="profilingTest" class="be.eliwan.profiling.service.ProfilingContainer">
        <property name="ringSize" value="128" />
    </bean>

You can now autowire the profiling container in your code and
register execution time for specific invocations.

    @Autowired
    @Qualifier("restProfiling")
    private ProfilingContainer profilingContainer;
    
    
    long start = System.currentTimeMillis();
    // do something
    profilingContainer.register("some.grouping", System.currentTimeMillis() - start);

In this example a qualifier was used to be able to indicate which
profiling container needs to be used. If there is only one, this can be
omitted.

### Using AOP to profile services

You can use AOP to register profiling information for the methods in
a bean. You can configure both the profiling container to use and the
group for the registration.

    <bean class="org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator">
        <property name="beanNames" value="service*"/>
        <property name="interceptorNames">
            <list>
                <value>myMethodProfilingInterceptor</value>
            </list>
        </property>
    </bean>

    <bean name="myMethodProfilingInterceptor" class="be.eliwan.profiling.aop.MethodProfilingInterceptor">
        <property name="group" value="test" />
        <property name="profilingContainer" ref="profilingTest" />
    </bean>
    
### Profiling JDBC access

There is also a profiling JDBC driver which registers profiling data
for most JDBC related calls (it attempts to not register calls which
should be instantaneous, however this can sometimes depend on the JDBC
driver, in which case the profiling will register too little).

When this driver is loaded using

    Class.forName("be.eliwan.profiling.jdbc.ProfilingDriver")

then you can prefix your JDBC connection string with "profiling:" to
assure that the profiling driver is used.

You still have to connect the profiling driver to your profiling
container though. This is for example be done using a service like
this.

    public class JdbcProfiling implements ProfilingListener, InitializingBean {

        @Autowired
        @Qualifier("jdbcMethodProfiling")
        private ProfilingContainer jdbcMethodProfilingContainer;
        
        /**
         * Register de profiling listener.
         */
        public void afterPropertiesSet() {
            ProfilingDriver.addListener(this);
        }
        
        @Override
        public void register(String group, long durationMillis) {
            jdbcMethodProfilingContainer.register(group, durationMillis);
        }
        
    }

### Making your profiling data available through JMX

You can use AOP to indicate which method (or which beans) need to be profiled.

    <bean id="mbeanServer" class="org.springframework.jmx.support.MBeanServerFactoryBean">
        <property name="locateExistingServerIfPossible" value="true"/>
    </bean>

    <bean id="exporter" class="org.springframework.jmx.export.MBeanExporter">
        <property name="beans">
            <map>
                <entry key="bean:name=profilingAopTestprofilingAopTest" value-ref="profilingTest"/>
            </map>
        </property>
        <property name="assembler">
            <bean class="org.springframework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler">
                <property name="managedInterfaces" value="be.eliwan.profiling.api.ProfilingBean" />
            </bean>
        </property>
    </bean>

How to use JConsole to connect to the MBeans
--------------------------------------------

If you have configured your system to surface the profiling beans as
MBeans, you can use JConsole to connect to your application.

To assure that you do not get RMI marshalling exceptions on the
profile bean invocations, you may need to add the profiling jar in the
classpath when running JConsole.

This can be done by invoking JConsole using a command like this (you
may need to fix the path to the jar):

    jconsole -J-Djava.class.path=$JAVA_HOME/lib/jconsole.jar:\
    $JAVA_HOME/lib/tools.jar:\
    ~/.m2/repository/be/eliwan/1.0/ew-profiling-api-1.0.jar:\
    ~/.m2/repository/be/eliwan/1.0/ew-profiling-core-1.0.jar
