<!DOCTYPE html>
<meta charset="utf-8">
<style>

    .node circle {
        fill: #fff;
        stroke: steelblue;
        stroke-width: 1.5px;
    }

    .node {
        font: 10px sans-serif;
    }

    .link {
        fill: none;
        stroke: #ccc;
        stroke-width: 1.5px;
    }

</style>
<body>
    <script src="//d3js.org/d3.v3.min.js"></script>
    <script>
        setDataset("flare.json");

        function setDataset(dsname) {
            d3.json("flare.json", function(error, root) {
                if (error)
                    throw error;

                var nodes = tree.nodes(root),
                        links = tree.links(nodes);

                var link = svg.selectAll(".link")
                        .data(links)
                        .enter().append("path")
                        .attr("class", "link")
                        .attr("d", diagonal);

                var node = svg.selectAll(".node")
                        .data(nodes)
                        .enter().append("g")
                        .attr("class", "node")
                        .attr("transform", function(d) {
                            return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")";
                        })

                node.append("circle")
                        .attr("r", 4.5);

                node.append("text")
                        .attr("dy", ".31em")
                        .attr("text-anchor", function(d) {
                            return d.x < 180 ? "start" : "end";
                        })
                        .attr("transform", function(d) {
                            return d.x < 180 ? "translate(8)" : "rotate(180)translate(-8)";
                        })
                        .text(function(d) {
                            return d.name;
                        });
            });
        }


        var diameter = 960;

        var tree = d3.layout.tree()
                .size([360, diameter / 2 - 120])
                .separation(function(a, b) {
                    return (a.parent == b.parent ? 1 : 2) / a.depth;
                });

        var diagonal = d3.svg.diagonal.radial()
                .projection(function(d) {
                    return [d.y, d.x / 180 * Math.PI];
                });

        var svg = d3.select("body").append("svg")
                .attr("width", diameter)
                .attr("height", diameter - 150)
                .append("g")
                .attr("transform", "translate(" + diameter / 2 + "," + diameter / 2 + ")");



        d3.select(self.frameElement).style("height", diameter - 150 + "px");

    </script>