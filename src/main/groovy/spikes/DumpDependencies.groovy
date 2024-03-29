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

package spikes

import com.tinkerpop.gremlin.groovy.Gremlin
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph

class DumpDependencies {

    Neo4jGraph g

    static {
        Gremlin.load()
    }

    static void main(String[] args) {
        def dumpDependencies = new DumpDependencies()

        try {
            dumpDependencies.init()
            dumpDependencies.printDependenciesOn('hermes-hotel-api')
        }
        finally {
            dumpDependencies.shutdown()
        }
    }

    void printDependenciesOn(String s) {
        def module = g.V.filter {it.name == s }.next()

        def dependencyLevels = [(module.name):1]

        module.in.loop(1) {
            dependencyLevels[it.object.name] = it.loops
            true
        }.each {}

        def modulesGroupedByLevel = dependencyLevels.groupBy { it.value }.sort { it.key }

        modulesGroupedByLevel.each {
            modulesGroupedByLevel[it.key] = it.value.collect {it.key}
        }

        modulesGroupedByLevel.each {
            println "Level $it.key"
            it.value.each {
                println it
            }
            println "-" * 30
        }
    }


    void init() {
        g = new Neo4jGraph('/tmp/neo4j2')
    }

    void shutdown() {
        g.shutdown()
    }
}
