import java.io.*;
import java.util.*;

class Participants {
    String identifier;
    int score;

    public Participants(String identifier) {
        this.identifier = identifier;
        this.score = 0;
    }
}

class RegionsSection {
    String name;
    List<Participants> contestants;
    int invalidVoteCounts;

    public RegionsSection(String name) {
        this.name = name;
        this.contestants = new ArrayList<>();
        this.invalidVoteCounts = 0;
    }

    public void addContestant(Participants contestant) {
        contestants.add(contestant);
    }

    public void incrementInvalidVotes() {
        invalidVoteCounts++;
    }
}

public class ElectionCounting {
    Map<Character, Participants> ParticipantsMap;
    List<RegionsSection> units;
    int totalInvalidVotes;

    public ElectionCounting() {
        ParticipantsMap = new HashMap<>();
        units = new ArrayList<>();
        totalInvalidVotes = 0;
        initializeParticipantss();
    }

    private void initializeParticipantss() {
        for (char c = 'A'; c <= 'Y'; c++) {
            Participants Participants = new Participants(String.valueOf(c));
            ParticipantsMap.put(c, Participants);
        }
    }

    private void processInputData(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            String currentUnitName = null;
            int invalidVotesUnit = 0;
            while ((line = br.readLine()) != null) {
                if (line.equals("&&")) break;
                if (line.equals("//")) continue;
                if (line.contains("/")) {
                    String[] parts = line.split("/");
                    if (parts.length >= 2) {
                        currentUnitName = parts[0];
                        RegionsSection unit = new RegionsSection(currentUnitName);
                        if (parts[1].length() > 4) continue;
                        for (char c : parts[1].toCharArray()) {
                            Participants Participants = ParticipantsMap.get(c);
                            if (Participants != null) unit.addContestant(Participants);
                        }
                        units.add(unit);
                    }
                } else if (!line.trim().isEmpty()) {
                    RegionsSection currentUnit = null;
                    for (RegionsSection unit : units) {
                        if (unit.name.equals(currentUnitName)) {
                            currentUnit = unit;
                            break;
                        }
                    }
                    if (currentUnit == null) continue;
                    String[] parts = line.split(" ");
                    String voterID = parts[0];
                    int invalidVotesPerVoter = 0;
                    if (parts.length > 4) {
                        invalidVotesPerVoter++;
                        totalInvalidVotes++;
                        invalidVotesUnit++;
                        currentUnit.incrementInvalidVotes();
                        continue;
                    }
                    for (int i = 1; i < parts.length; i++) {
                        String preferences = parts[i];
                        if (preferences.length() > 3) {
                            invalidVotesPerVoter++;
                            totalInvalidVotes++;
                            invalidVotesUnit++;
                            currentUnit.incrementInvalidVotes();
                            continue;
                        }
                        for (int j = 0; j < preferences.length(); j++) {
                            char c = preferences.charAt(j);
                            Participants Participants = ParticipantsMap.get(c);
                            if (Participants != null) {
                                if (j == 0) Participants.score += 3;
                                else if (j == 1) Participants.score += 2;
                                else Participants.score += 1;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayResults() {
        Participants chiefOfficer = null;
        for (Participants Participants : ParticipantsMap.values()) {
            if (chiefOfficer == null || Participants.score > chiefOfficer.score) chiefOfficer = Participants;
        }
        System.out.println("Chief Officer: " + chiefOfficer.identifier);
        System.out.println("Total invalid votes: " + totalInvalidVotes);
        for (RegionsSection unit : units) {
            Participants regionalLead = null;
            for (Participants Participants : unit.contestants) {
                if (regionalLead == null || Participants.score > regionalLead.score) regionalLead = Participants;
            }
            System.out.println(unit.name + " regional lead: " + regionalLead.identifier);
            System.out.println("Invalid Votes: " + unit.invalidVoteCounts);
            System.out.println("Contestants  Points:");
            for (int i = 1; i <= unit.contestants.size(); i++) {
                Participants Participants = unit.contestants.get(i - 1);
                System.out.println(i + " " + Participants.identifier + " " + Participants.score);
            }
            System.out.println();
        }
    }

    public void runProcess(String fileName) {
        processInputData(fileName);
        displayResults();
    }

    public static void main(String[] args) {
        ElectionCounting ElectionCounting = new ElectionCounting();
        ElectionCounting.runProcess("C://voting.dat");
    }
}