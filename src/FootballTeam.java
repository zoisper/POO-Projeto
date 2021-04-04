import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Write a description of class Team here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class FootballTeam implements Comparable<FootballTeam>
{
    // instance variables - replace the example below with your own
    private String name;
    private ArrayList<FootballPlayer> squad;
    private ArrayList<FootballPlayer> bench;
    private ArrayList<FootballPlayer> starting;


    private int points;

    /**
     * Constructor for objects of class Team
     */
    public FootballTeam(){
        this.name = "Sem Nome";
        this.squad = new ArrayList<FootballPlayer>();
        this.starting =  new ArrayList<FootballPlayer>();
        this.bench = new ArrayList<FootballPlayer> ();
        this.points = 0;
    }
    
    public FootballTeam(String name, ArrayList<FootballPlayer> squad, ArrayList<FootballPlayer> starting, ArrayList<FootballPlayer> bench, int points){
        this.name = name;
        setSquad(squad);
        setStarting(starting);
        setBench(bench);
        this.points = points;
    }
    
    public FootballTeam(FootballTeam team){
        this.name = team.getName();
        setSquad(team.getSquad());
        setStarting(team.getStarting());
        setBench(team.getBench());
        this.points = team.getPoints();
    }
    
    public String getName(){
        return this.name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public ArrayList<FootballPlayer> getSquad(){
        ArrayList<FootballPlayer> ret = new ArrayList<FootballPlayer>();
        for (FootballPlayer fp : this.squad)
            ret.add(fp.clone());
        return ret;
    }
    
    public void setSquad(ArrayList<FootballPlayer> squad){
        this.squad = new ArrayList<FootballPlayer>();
        for (FootballPlayer fp : squad)
            this.squad.add(fp.clone());
    }

    public ArrayList<FootballPlayer> getStarting() {
        ArrayList<FootballPlayer> ret = new ArrayList<FootballPlayer>();
        for (FootballPlayer fp : this.starting)
            ret.add(fp.clone());
        return ret;
    }

    public void setStarting(ArrayList<FootballPlayer> starting){
        this.starting = new ArrayList<FootballPlayer>();
        for(FootballPlayer fp : starting){
            FootballPlayer aux = getPlayerFromList(squad, fp);
            this.starting.add(aux);
        }
    }

    public ArrayList<FootballPlayer> getBench() {
        ArrayList<FootballPlayer> ret = new ArrayList<FootballPlayer>();
        for (FootballPlayer fp : this.bench)
            ret.add(fp.clone());
        return ret;
    }

    public void setBench(ArrayList<FootballPlayer> bench){
        this.bench = new ArrayList<FootballPlayer>();
        for(FootballPlayer fp : bench){
            FootballPlayer aux = getPlayerFromList(squad, fp);
            this.bench.add(aux);
        }
    }



    public int getPoints(){
        return this.points;
    }
    
    public void setPoints(int points){
        if(points >=0)
            this.points = points;
    }
    
    public void increasePoints(int inc){
        if(inc == 1 || inc == 3)
            this.points += inc;
    }

    
    public void resetPoints(){
        this.points = 0;
    }
    
    public FootballTeam clone(){
        return new FootballTeam(this);
    }
    
    public boolean equals(Object o){
        if (o == this) return true;
        if(o == null || o.getClass() != this.getClass()) return false;
        FootballTeam t = (FootballTeam) o;
        return t.getName() == this.name && t.getSquad().equals(this.getSquad());
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Nome da Equipa: ").append(this.name).append("\nPlantel:\n");
        for (Player p : squad)
            sb.append(p).append("\n");
       return sb.toString(); 
    }


    
    public void squadAddPlayer(FootballPlayer fp){
        if(!squad.contains(fp)){
            FootballPlayer copy = fp.clone();
            squad.add(copy);
            startingAddPlayer(copy);
            if(!starting.contains(copy))
                benchAddPlayer(copy);
        }
    }

    public void squadRemovePlayer(FootballPlayer fp){
        this.squad.remove(fp);
        if(this.starting.contains(fp))
            startingRemovePlayer(fp);
        else
            benchRemovePlayer(fp);
    }

    private FootballPlayer getPlayerFromList(ArrayList<FootballPlayer> a, int index){
        if(index <a.size())
            return a.get(index);
        return null;
    }
    private FootballPlayer getPlayerFromList(ArrayList<FootballPlayer> a, FootballPlayer fp){
        int index = a.indexOf(fp);
        return getPlayerFromList(a,index);
    }

    public FootballPlayer squadGetPlayer(FootballPlayer fp){
        int index = squad.indexOf(fp);
        return squadGetPlayer(index);
    }

   public FootballPlayer squadGetPlayer(int index){
        FootballPlayer ret = getPlayerFromList(squad,index);
        if(ret != null)
            return ret.clone();
        return null;
    }

    public FootballPlayer startingGetPlayer(int index){
        FootballPlayer ret = getPlayerFromList(starting, index);
        if(ret != null)
            return ret.clone();
        return null;
    }

    public FootballPlayer benchGetPlayer(int index){
        FootballPlayer ret = getPlayerFromList(bench, index);
        if(ret != null)
            return ret.clone();
        return null;
    }



    public void moveToBench(int index){
        FootballPlayer fp = getPlayerFromList(squad, index);
        if(fp != null && starting.contains(fp)){
            startingRemovePlayer(fp);
            benchAddPlayer(fp);
        }
    }

    public void moveToBench(FootballPlayer fp){
        int index = squad.indexOf(fp);
        moveToBench(index);
    }

    public void moveToStarting(int index){
        FootballPlayer fp = getPlayerFromList(squad, index);
        if(fp != null){
            int flag = startingAddPlayer(fp);
            if(flag == 1){
                startingRemovePlayer(fp);
                benchAddPlayer(fp);
            }
        }
    }


    public void moveToStarting (FootballPlayer fp){
        int index = squad.indexOf(fp);
        moveToStarting(index);
    }

    private int startingAddPlayer(FootballPlayer fp){
        if(!starting.contains(fp) && starting.size() <11) {
            if (fp instanceof GoalKeeper && filterByPosition(starting, Position.GOALKEEPER).size() == 0){
                starting.add(fp);
                return 1;
            }
            if(fp instanceof  Defender && filterByPosition(starting, Position.DEFENDER).size() < 2){
                starting.add(fp);
                return 1;
            }
            if(fp instanceof  Winger && filterByPosition(starting, Position.WINGER).size() < 2){
                starting.add(fp);
                return 1;
            }
            if(fp instanceof  MidFielder && filterByPosition(starting, Position.MIDFIELDER).size() < 4){
                starting.add(fp);
                return 1;
            }
            if(fp instanceof  Striker && filterByPosition(starting, Position.STRIKER).size() < 2){
                starting.add(fp);
                return 1;
            }
        }
        return 0;
    }

    private void startingRemovePlayer(FootballPlayer fp){
        starting.remove(fp);
    }

    private void benchAddPlayer(FootballPlayer fp){
        if(!bench.contains(fp))
            bench.add(fp);
    }

    private void benchRemovePlayer(FootballPlayer fp){
        bench.remove(fp);
    }

    private void setPlayer(ArrayList<FootballPlayer> a, FootballPlayer fp){
        int index = a.indexOf(fp);
        if(index >=0)
            a.set(index, fp);
    }

    public void updatePlayer(FootballPlayer fp){
        FootballPlayer copy = fp.clone();
        setPlayer(this.squad, copy);
        if (this.starting.contains(fp))
            setPlayer(this.starting, copy);
        else
            setPlayer(this.bench, copy);
    }

    private ArrayList<FootballPlayer> filterByPosition(ArrayList<FootballPlayer> a, Position pos){
        return (ArrayList<FootballPlayer>) a.stream().filter(p->p.getPosition() == pos).collect(Collectors.toList());
    }
    
    public int defensivePower(){
        ArrayList<FootballPlayer> gks = filterByPosition(this.starting, Position.GOALKEEPER);
        ArrayList<FootballPlayer> dfs = filterByPosition(this.starting, Position.DEFENDER);
        ArrayList<FootballPlayer> mds = filterByPosition(this.starting, Position.MIDFIELDER);
        ArrayList<FootballPlayer> wgs = filterByPosition(this.starting, Position.WINGER);
        ArrayList<FootballPlayer> sts = filterByPosition(this.starting, Position.STRIKER);
        double g = 0, d = 0, m = 0, w = 0, s = 0;
        if(gks.size()>0)
            g = (double) gks.stream().mapToInt(FootballPlayer::overall).sum()/gks.size() * 0.2;
        if(dfs.size()>0)
            d = (double) dfs.stream().mapToInt(FootballPlayer::overall).sum()/dfs.size() * 0.3;
        if(mds.size()>0)
            m = (double) mds.stream().mapToInt(FootballPlayer::overall).sum()/mds.size() * 0.2;
        if(wgs.size()>0)
            w = (double) wgs.stream().mapToInt(FootballPlayer::overall).sum()/wgs.size() * 0.2;
        if(sts.size()>0)
            s = (double) sts.stream().mapToInt(FootballPlayer::overall).sum()/sts.size() * 0.1;
        return (int) (d+g+m+w+s);
    }
    
    public int ofensivePower(){
        ArrayList<FootballPlayer> dfs = filterByPosition(this.starting,Position.DEFENDER);
        ArrayList<FootballPlayer> mds = filterByPosition(this.starting, Position.MIDFIELDER);
        ArrayList<FootballPlayer> wgs = filterByPosition(this.starting, Position.WINGER);
        ArrayList<FootballPlayer> sts = filterByPosition(this.starting, Position.STRIKER);
        double d = 0, m = 0, w = 0, s = 0;
        if(dfs.size()>0)
            d = (double) dfs.stream().mapToInt(FootballPlayer::overall).sum()/dfs.size() * 0.1;
        if(mds.size()>0)
            m = (double) mds.stream().mapToInt(FootballPlayer::overall).sum()/mds.size() * 0.3;
        if(wgs.size()>0)
            w = (double) wgs.stream().mapToInt(FootballPlayer::overall).sum()/wgs.size() * 0.2;
        if(sts.size()>0)
            s = (double) sts.stream().mapToInt(FootballPlayer::overall).sum()/sts.size() * 0.4;
        return (int) (d+m+w+s);
    }
    
    public int overallPower(){
        return starting.stream().mapToInt(FootballPlayer::overall).sum()/starting.size();
    }

    public int compareTo(FootballTeam team){

        return this.getName().compareTo(team.getName());

    }



   
}
