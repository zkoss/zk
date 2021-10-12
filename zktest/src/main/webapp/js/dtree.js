
var m = [20, 120, 20, 120],
    i = 0,
    root;

var tree;

var diagonal = d3.svg.diagonal()
    .projection(function(d) { return [d.y, d.x]; });

var vis, w, h;

function getMaxNodeSize(node) {
	var cn = 0;
	if (node.children) {
		cn = node.children.length;
		var levelInfos = {depth: 0, nodeSizes: [cn]};
		for (var i = 0; i < cn; i++) {
			if (node.children[i].children) {
				getNodeSize(node.children[i], levelInfos);
				levelInfos.depth--;
			}
		}
		var maxSize = 0;
		for (var ns = levelInfos.nodeSizes, i = 0, j = ns.length; i < j; i++)
			if (ns[i] > maxSize)
				maxSize = ns[i];
		return maxSize;
	}
	return 0;
}
function getNodeSize(node, levelInfos) {
	if (node.children) {
		var cn = node.children.length;
		levelInfos.depth++;
		if (levelInfos.nodeSizes[levelInfos.depth]) {
			levelInfos.nodeSizes[levelInfos.depth] += cn;
		} else {
			levelInfos.nodeSizes[levelInfos.depth] = cn;
		}
		for (var i = 0; i < cn; i++) {
			if (node.children[i].children) {
				getNodeSize(node.children[i], levelInfos);
				levelInfos.depth--;
			}
		}
	}
}
window.DrawTree = function(json) {
	var maxNodeSize = getMaxNodeSize(json);
	var zkcontent = jq('$host');
	 w = jq.innerWidth() - 50 - m[1] - m[3];
	 h = jq.innerHeight() - 100 - zkcontent.height() - m[0] - m[2];
	 h = Math.max(110 * maxNodeSize, h);
  if (!vis) {
	 tree = d3.layout.tree().size([h, w]);
	  vis = d3.select("#body").append("svg:svg")
	    .attr("width", w + m[1] + m[3])
	    .attr("height", h + m[0] + m[2])
	  .append("svg:g")
	    .attr("transform", "translate(" + m[3] + "," + m[0] + ")");
  }
  root = json;
  root.x0 = h / 2;
  root.y0 = 0;

  function toggleAll(d) {
    if (d.children) {
      d.children.forEach(toggleAll);
      toggle(d);
    }
  }

  // Initialize the display to show a few nodes.
  //root.children.forEach(toggleAll);

  update(root);
};

function update(source) {
  var duration = d3.event && d3.event.altKey ? 5000 : 500;

  // Compute the new tree layout.
  var nodes = tree.nodes(root).reverse();

  // Normalize for fixed-depth.
  nodes.forEach(function(d) { d.y = d.depth * 260;});

  // Update the nodes…
  var node = vis.selectAll("g.node")
      .data(nodes, function(d) { return d.id || (d.id = ++i); });

  // Enter any new nodes at the parent's previous position.
  var nodeEnter = node.enter().append("svg:g")
      .attr("class", "node")
      .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
      .on("click", function(d) { toggle(d); update(d); });

  nodeEnter.append("svg:circle")
      .attr("r", 1e-6)
      .attr('class', function(d) {return d.real ? 'component' : ''})
      .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

  nodeEnter.append("svg:text")
      .attr("x", function(d) { return d.depth ? 10 : -10; })
      .attr("dy", ".35em")
      .attr("text-anchor", function(d) { return d.depth ? "start" : "end"; })
      .text(function(d) { return d.name; })
      .style("fill-opacity", 1e-6);

  nodeEnter.append("svg:foreignObject")
  .attr("x", function(d) { return d.depth ? 10 : -100; })
  .attr("y", function(d) {
	  var base = -20; 
	  return base * (d.allChildren ? (d.allChildren.length / 70 + 1) : 1);
  })
  .attr("dy", ".35em")
  .attr("class", 'allChildren')
  .attr("text-anchor", "start")
  .attr('width', "300")
  .attr('height', "200")
  .append("div")
  .style("fill-opacity", function (d) { return d.real ? 0 : 1})
  .attr('style', 'font-size:12px;color:rgb(148, 88, 145);font-weight:bold')
  .text(function(d) { return d['allChildren']; });

  var indent = 0;
  jq(['prev', 'first', 'last', 'next']).each(function () {
	  nodeEnter.append("svg:text")
	      .attr("x", 10)
	      .attr("y", indent+=15)
	      .attr("dy", ".35em")
	      .attr("class", this)
	      .attr("text-anchor", "start")
	      .attr('xml:space', 'preserve')
	      .style("display", function (d) { return d.real ? 'none' : 'block'})
	      .text((function (v) {return function(d) { return v + ':     ' + (d[v] == 'null' ? '' : d[v]); };})(this));
  });
  
  // Transition nodes to their new position.
  var nodeUpdate = node.transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

  nodeUpdate.select("circle")
      .attr("r", 4.5)
      .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

  nodeUpdate.select("text")
      .style("fill-opacity", 1);

  // Transition exiting nodes to the parent's new position.
  var nodeExit = node.exit().transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
      .remove();

  nodeExit.select("circle")
      .attr("r", 1e-6);

  nodeExit.select("text")
      .style("fill-opacity", 1e-6);

  // Update the links…
  var link = vis.selectAll("path.link")
      .data(tree.links(nodes), function(d) { return d.target.id; });

  // Enter any new links at the parent's previous position.
  link.enter().insert("svg:path", "g")
      .attr("class", "link")
      .attr("d", function(d) {
        var o = {x: source.x0, y: source.y0};
        return diagonal({source: o, target: o});
      })
    .transition()
      .duration(duration)
      .attr("d", diagonal);

  // Transition links to their new position.
  link.transition()
      .duration(duration)
      .attr("d", diagonal);

  // Transition exiting nodes to the parent's new position.
  link.exit().transition()
      .duration(duration)
      .attr("d", function(d) {
        var o = {x: source.x, y: source.y};
        return diagonal({source: o, target: o});
      })
      .remove();

  // Stash the old positions for transition.
  nodes.forEach(function(d) {
    d.x0 = d.x;
    d.y0 = d.y;
  });
}

// Toggle children.
function toggle(d) {
  if (d.children) {
    d._children = d.children;
    d.children = null;
  } else {
    d.children = d._children;
    d._children = null;
  }
}