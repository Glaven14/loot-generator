package edu.grinnell.csc207.lootgenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

/**
 * 
 */
public class LootGenerator {
    //The path to the dataset (either the small or large set).
    private static final String DATA_SET = "data/large";
    
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Scanner userScanner = new Scanner(System.in);
        String input = "y";
        while (!(input.equals("n") || input.equals("no"))) {
            if (input.equals("y") || input.equals("yes")) {
                String retmon = pickMonster();
                System.out.println("Fighting " + retmon + '\n' 
                                   + "You have slain " + retmon + "!" + '\n' 
                                   + retmon + " dropped:" + '\n' + '\n');
                String retTC = fetchTreasureClass(retmon);
                System.out.println(generateBaseItem(retTC));
            }
            System.out.println('\n' + "Fight again [y/n]?");
            input = userScanner.next();
            input = input.toLowerCase();
        }
        userScanner.close();

    }

    static String pickMonster() throws IOException {
        Random rand = new Random();
        Path monstatsPath = Paths.get(DATA_SET + "/" + "monstats.txt");
        Scanner monCount = new Scanner(monstatsPath);
        int lines = 0;
        String chosenLine = null;
        while (monCount.hasNextLine()) {
            chosenLine = monCount.nextLine();
            lines++;
        }
        int monChosen = rand.nextInt(lines + 1);
        monCount.close();
        Scanner monstatsScan = new Scanner(monstatsPath);
        while (monChosen > 0) {
            chosenLine = monstatsScan.nextLine();
            monChosen--;
        }
        String retMon;
        Scanner monstatsScan2 = new Scanner(chosenLine);

        monstatsScan2.useDelimiter("\\t");
        retMon = monstatsScan2.next();
        monstatsScan2.close();
        monstatsScan.close();
        return retMon;
    }

    static String fetchTreasureClass(String monster) throws IOException {
        Path monstatsPath = Paths.get(DATA_SET + "/" + "monstats.txt");
        Scanner monstatsScan = new Scanner(monstatsPath);
        String chosenLine = monstatsScan.nextLine();
        String monsterTC = "";
        monstatsScan.useDelimiter("\\t");
        while (monstatsScan.hasNextLine()) {
            
            String check = monstatsScan.next();
            if (monster.equals(check)) {
                chosenLine = monstatsScan.nextLine();
            } else {
                monstatsScan.nextLine();
            }
        }
        int indOfTC = chosenLine.lastIndexOf('\t');
        monsterTC = chosenLine.substring(indOfTC + 1);
        monstatsScan.close();
        return fetchTreasureClassH(monsterTC);
    }

    static String fetchTreasureClassH(String treasureClass) throws IOException {
        Random rand = new Random();
        int itemChosen = rand.nextInt(3); //3 items in each TC
        Path treClaPath = Paths.get(DATA_SET + "/" + "TreasureClassEx.txt");
        Scanner treClaScan = new Scanner(treClaPath);
        treClaScan.useDelimiter("\\t");
        String chosenLine = null;
        while (treClaScan.hasNextLine()) {
            String ch = treClaScan.next();
            boolean check = treasureClass.equals(ch);            
            if (check) {
                chosenLine = treClaScan.nextLine();
            } else {
                treClaScan.nextLine();
            }
        }
        if (chosenLine == null) {
            treClaScan.close();
            return treasureClass;
        } else {
            String[] treClaLine = chosenLine.split("\\t");
            treasureClass = treClaLine[itemChosen + 1];
            treClaScan.close();
            return fetchTreasureClassH(treasureClass);
        }
    }


    static String generateBaseItem(String item) throws IOException { //Is this necessary?
        String retname;
        String extrastats;
        String[] temp = generateAffix(item);
        retname = temp[0];
        extrastats = temp[1];
        return retname + '\n' + generateBaseStats(item) + '\n' + extrastats;
    }
    static String generateBaseStats(String item) throws IOException {
        Random rand = new Random();
        Path itemPath = Paths.get(DATA_SET + "/" + "armor.txt");
        Scanner itemScan = new Scanner(itemPath);
        itemScan.useDelimiter("\\t");
        String chosenLine = null;
        while (itemScan.hasNextLine()) {
            String ch = itemScan.next();
            boolean check = item.equals(ch);            
            if (check) {
                chosenLine = itemScan.nextLine();
            } else {
                itemScan.nextLine();
            }
        }
        String[] itemLine = chosenLine.split("\\t");
        int minac = Integer.parseInt(itemLine[1]);
        int maxac = Integer.parseInt(itemLine[2]);
        itemScan.close();
        int acChosen = rand.nextInt((maxac - minac) + 1) + minac;
        String ret = "Defense: ";
        ret += acChosen;
        return ret;
    }

    static String[] generateAffix(String item) throws IOException {
        Random rand = new Random();
        int preffix = rand.nextInt(2); // 0 - No, 1 - Yes
        int suffix = rand.nextInt(2); // 0 - No, 1 - Yes
        String retname = "";
        String retvalue = "";
        if (preffix == 1) {
            String[] temp = generatePreffix();
            retname += temp[0] + " "; 
            retvalue += temp[2] + " " + temp[1] + '\n';
        }
        retname += item + " ";
        if (suffix == 1) {
            String[] temp = generateSuffix();
            retname += temp[0] + " "; 
            retvalue += temp[2] + " " + temp[1];
        }
        String[] ret = {retname, retvalue};
        return ret;
    }

    static String[] generatePreffix() throws IOException {
        Random rand = new Random();
        String[] name = new String[3];
        Path prePath = Paths.get(DATA_SET + "/" + "MagicPrefix.txt");
        Scanner preCount = new Scanner(prePath);
        int lines = 0;
        String chosenLine = null;
        while (preCount.hasNextLine()) {
            chosenLine = preCount.nextLine();
            lines++;
        }
        int preChosen = rand.nextInt(lines + 1);
        preCount.close();
        Scanner preScan = new Scanner(prePath);
        while (preChosen > 0) {
            chosenLine = preScan.nextLine();
            preChosen -= 1;
        }
        String[] preLine = chosenLine.split("\\t");
        name[0] = preLine[0];
        int minmod = Integer.parseInt(preLine[2]);
        int maxmod = Integer.parseInt(preLine[3]);
        preScan.close();
        name[1] = Integer.toString(rand.nextInt((maxmod - minmod) + 1) + minmod);
        name[2] = preLine[1];
        return name;
    }

    static String[] generateSuffix() throws IOException {
        Random rand = new Random();
        String[] name = new String[3];
        Path suPath = Paths.get(DATA_SET + "/" + "MagicSuffix.txt");
        Scanner suCount = new Scanner(suPath);
        int lines = 0;
        String chosenLine = null;
        while (suCount.hasNextLine()) {
            chosenLine = suCount.nextLine();
            lines++;
        }
        int preChosen = rand.nextInt(lines + 1);
        suCount.close();
        Scanner suScan = new Scanner(suPath);
        while (preChosen > 0) {
            chosenLine = suScan.nextLine();
            preChosen -= 1;
        }
        String[] suLine = chosenLine.split("\\t");
        name[0] = suLine[0];
        int minmod = Integer.parseInt(suLine[2]);
        int maxmod = Integer.parseInt(suLine[3]);
        suScan.close();
        name[1] = Integer.toString(rand.nextInt((maxmod - minmod) + 1) + minmod);
        name[2] = suLine[1];
        return name;
    }
}
