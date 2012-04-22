package spikes
/**
 * Copyright 2012 Alessandro Novarini
 *
 * This file is part of the iwtr project.
 *
 * Iwtr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import com.tinkerpop.gremlin.groovy.Gremlin
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph
import com.tinkerpop.blueprints.pgm.Vertex


public class IvyDependencies {

    Neo4jGraph g

    static {
        Gremlin.load()
    }

    void createDependencyTree(String ivyFile) {
        def ivyModule = new XmlSlurper().parse ivyFile
        def moduleName = ivyModule.info.@module.text()

        def storedModule = getOrCreateModule moduleName

        def dependencies = ivyModule.dependencies.dependency.collect { it.@name.text() }
        dependencies.each {
            dependencyName ->
            def storedDependency = getOrCreateModule dependencyName
            getOrCreateDependency(storedModule, storedDependency)
        }
    }

    void getOrCreateDependency(Vertex storedModule, Vertex storedDependency) {
        def storedLink = storedModule.out.filter {
            it.name == storedDependency.name
        }
        if (!storedLink.hasNext()) {
            g.addEdge null, storedModule, storedDependency, 'depends_on'
            println "Created dependency on $storedDependency.name for $storedModule.name"
        }
        else {
            println "Dependency on $storedDependency.name for $storedModule.name already exists"
        }
    }

    Vertex getOrCreateModule(def moduleName) {
        def storedModule = g.V.filter { it.name == moduleName }

        if (!storedModule.hasNext()) {
            storedModule = g.addVertex([name: (moduleName)])
            println "Created module $moduleName"
            return storedModule
        }
        else {
            println "Module $moduleName already exists"
        }
        storedModule.next()
    }

    public static void main(String[] args) {

        def ivyDependencies = new IvyDependencies()
        try {
            ivyDependencies.init()

            def p = ~/ivy.xml/
            def clos

            clos = {
                it.eachDir clos
                it.eachFileMatch(p) {
                    ivyDependencies.createDependencyTree it.canonicalPath
                }
            }

            clos new File('/Users/dev/Development/hcom/HcomModules/modules')
        }
        finally {
            ivyDependencies.shutdown()
        }
    }

    void init() {
        g = new Neo4jGraph('/tmp/neo4j2')
    }

    void shutdown() {
        g.shutdown()
    }
}