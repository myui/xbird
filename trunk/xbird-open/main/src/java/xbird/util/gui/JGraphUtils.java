/*
 * @(#)$Id: JGraphUtils.java 3619 2008-03-26 07:23:03Z yui $
 *
 * Copyright 2006-2008 Makoto YUI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Makoto YUI - initial implementation
 */
package xbird.util.gui;

import java.util.*;

import org._3pq.jgrapht.DirectedGraph;
import org._3pq.jgrapht.ext.JGraphModelAdapter;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.layout.*;

public final class JGraphUtils {

    private JGraphUtils() {}

    public static void applyOrderedTreeLayout(DirectedGraph graphModel, JGraphModelAdapter graphAdapter, JGraph graph) {
        List<DefaultGraphCell> roots = new ArrayList<DefaultGraphCell>();
        Iterator vertexIter = graphModel.vertexSet().iterator();
        while(vertexIter.hasNext()) {
            Object vertex = vertexIter.next();
            if(graphModel.inDegreeOf(vertex) == 0) {
                roots.add(graphAdapter.getVertexCell(vertex));
            }
        }
        final TreeLayoutAlgorithm algo = new OrderedTreeLayoutAlgorithm();
        algo.setCenterRoot(true);
        algo.setLevelDistance(50);
        algo.setNodeDistance(60);
        JGraphLayoutAlgorithm.applyLayout(graph, algo, roots.toArray(), null);
    }

}
