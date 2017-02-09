import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Collections;


public class AStarExp_912260569_999706363 implements AIModule
{
    public class Cell{
        public int x,y;
        public Point self;
        public double height, cost, heuristic;
        public String key;
        public Cell parent;

        public Cell(final TerrainMap map, final Point x){
            this.self = x;
            this.x = x.x;
            this.y = x.y;
            this.key = this.x + "," + this.y;
            this.height = map.getTile(x);
        }
    }

    public class compareCell implements Comparator<Cell>{
        public int compare(Cell x, Cell y) {
            if ((x.cost + x.heuristic) > (y.cost + y.heuristic))
                return 1;
            if ((x.cost + x.heuristic) < (y.cost + y.heuristic))
                return -1;
            else
                return 0;
        }
    }

    public List<Point> createPath(final TerrainMap map)
    {
        HashMap<String, Cell> opened = new HashMap<String, Cell> ();
        HashMap<String, Cell> closed = new HashMap<String, Cell> ();
        Comparator<Cell> comparator = new compareCell();
        PriorityQueue<Cell> visited = new PriorityQueue<Cell> (10, comparator);

        Cell CurrentCell = new Cell(map, map.getStartPoint());
        CurrentCell.parent = null;
        CurrentCell.cost = 0;
        CurrentCell.heuristic = getHeuristic(map, map.getStartPoint(), map.getEndPoint());
        Cell endCell = new Cell(map, map.getEndPoint());
        visited.add(CurrentCell);
        closed.put(CurrentCell.key, CurrentCell);

        while (map.getEndPoint().x != CurrentCell.x || map.getEndPoint().y != CurrentCell.y)
        {
            Point[] neighbors = map.getNeighbors(CurrentCell.self);
            double[] values = new double[8];
            for (Point neighbor : neighbors) {
                Cell temp = new Cell(map, neighbor);
                if (closed.containsKey(temp.key))
                    continue;
                temp.cost = CurrentCell.cost + map.getCost(CurrentCell.self, neighbor);
                temp.heuristic = getHeuristic(map, neighbor, map.getEndPoint());
                if (opened.containsKey(temp.key)) {
                    Cell x = opened.get(temp.key);
                    if (x.cost > temp.cost) {
                        temp.parent = CurrentCell;
                        opened.remove(temp.key);
                        opened.put(temp.key, temp);
                        visited.remove(x);
                        visited.add(temp);
                    }
                } else {
                    temp.parent = CurrentCell;
                    opened.put(temp.key, temp);
                    visited.add(temp);
                }
            }
            CurrentCell = visited.poll();
        }
        return returnFinalPath(map, CurrentCell);
    }

    public List<Point> returnFinalPath(final TerrainMap map, Cell CurrentCell){
        final ArrayList<Point> path = new ArrayList<Point>();

        while(CurrentCell.parent != null){
            path.add(CurrentCell.self);
            CurrentCell = CurrentCell.parent;
        }
        path.add(CurrentCell.self);
        Collections.reverse(path);
        return path;
    }

    private double getHeuristic(final TerrainMap map, final Point curPoint, final Point endPoint)
    {
        double z = map.getTile(endPoint) - map.getTile(curPoint);
        double x = Math.abs(endPoint.x - curPoint.x);
        double y = Math.abs(endPoint.y - curPoint.y);
        double manhattan;

        if (z > 0)
        {
            if (x >= y)
                manhattan = Math.floor(y + (x - y));
            else
                manhattan = Math.floor(x + (y - x));

            if (manhattan >= z)
                return (z*2) + (manhattan - z);
            else
                return manhattan * Math.pow(2, (z / manhattan));

        }

        if (z < 0)
        {
            if (x >= y)
                manhattan = Math.floor(y + (x - y));
            else
                manhattan = Math.floor(x + (y - x));

            z = Math.abs(z);

            if (manhattan >= z)
                return (z / 2) + (manhattan - z);
            else
                return manhattan / Math.pow(2, (z / manhattan));
        }

        else
        {
            if (x >= y)
                return Math.floor(y + (x - y));
            else
                return Math.floor(x + (y - x));
        }
    }
}
