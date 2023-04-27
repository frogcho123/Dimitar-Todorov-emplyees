import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static class Project {
        int empId;
        int projectId;
        LocalDate dateFrom;
        LocalDate dateTo;

        Project(int empId, int projectId, LocalDate dateFrom, LocalDate dateTo) {
            this.empId = empId;
            this.projectId = projectId;
            this.dateFrom = dateFrom;
            this.dateTo = dateTo;
        }

        public int getEmpId() {
            return empId;
        }

        public void setEmpId(int empId) {
            this.empId = empId;
        }

        public int getProjectId() {
            return projectId;
        }

        public void setProjectId(int projectId) {
            this.projectId = projectId;
        }

        public LocalDate getDateFrom() {
            return dateFrom;
        }

        public void setDateFrom(LocalDate dateFrom) {
            this.dateFrom = dateFrom;
        }

        public LocalDate getDateTo() {
            return dateTo;
        }

        public void setDateTo(LocalDate dateTo) {
            this.dateTo = dateTo;
        }
    }

    public static void main(String[] args) {
        String filePath = "D:\\IdeaProjects\\Dimitar-Todorov-emplyees\\src\\employee_projects.csv";
        try {
            List<Project> projects = readEmployeeProjects(filePath);
            int[] longestPair = findLongestWorkingPair(projects);
            System.out.printf("Employees %d and %d have worked together for %d days.%n", longestPair[0], longestPair[1], longestPair[2]);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private static List<Project> readEmployeeProjects(String filePath) throws IOException {
        List<Project> projects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int empId = Integer.parseInt(parts[0]);
                int projectId = Integer.parseInt(parts[1]);
                LocalDate dateFrom = LocalDate.parse(parts[2], DATE_FORMATTER);
                LocalDate dateTo = parts[3].equals("NULL") ? LocalDate.now() : LocalDate.parse(parts[3], DATE_FORMATTER);
                projects.add(new Project(empId, projectId, dateFrom, dateTo));
            }
        }
        return projects;
    }

    private static int[] findLongestWorkingPair(List<Project> projects) {
        int[] longestPair = new int[3];
        Map<Map<Integer,Integer>,Integer> mapResult= new HashMap<>();
        for (int i = 0; i < projects.size() - 1; i++) {
            for (int j = i + 1; j < projects.size(); j++) {
                Project proj1 = projects.get(i);
                Project proj2 = projects.get(j);
                if (proj1.projectId == proj2.projectId) {
                    int days = calculateOverlap(proj1.getDateFrom(), proj1.getDateTo(), proj2.getDateFrom(), proj2.getDateTo());
                    int emplId1=proj1.empId;
                    int emplId2=proj2.empId;
                    Map<Integer,Integer> emplsId=new HashMap<>();
                    emplsId.put(emplId1,emplId2);
                    if(mapResult.containsKey(emplsId)){
                        mapResult.put(emplsId,days+mapResult.get(emplsId));
                    }
                    else {
                        mapResult.put(emplsId,days);
                    }
                    longestPair=result(mapResult);

                }
            }
        }
        return longestPair;
    }

    private static int calculateOverlap(LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {
        int overlap;
        if (startDate1.isAfter(endDate2) || startDate2.isAfter(endDate1)) {
            overlap = 0;
        } else if (startDate1.isBefore(startDate2) && endDate1.isBefore(endDate2)) {
            overlap = (int) ChronoUnit.DAYS.between(startDate2, endDate1) + 1;
        } else if (startDate2.isBefore(startDate1) && endDate2.isBefore(endDate1)) {
            overlap = (int) ChronoUnit.DAYS.between(startDate1, endDate2) + 1;
        } else if (startDate2.isBefore(startDate1) && endDate2.isAfter(endDate1)) {
            overlap = (int) ChronoUnit.DAYS.between(startDate1, endDate1) + 1;
        } else {
            overlap = (int) ChronoUnit.DAYS.between(startDate2, endDate2) + 1;
        }
        return overlap;
    }

    private static int[] result(Map<Map<Integer,Integer>,Integer> map){
        int[] longestPair = new int[3];
        int maxDays=0;
        for (Map.Entry<Map<Integer,Integer>,Integer> entry : map.entrySet()) {
            if(entry.getValue()>maxDays){
                Map<Integer,Integer> temp=entry.getKey();
                for(Map.Entry<Integer,Integer> emplIds : temp.entrySet()){
                    longestPair[0]=emplIds.getKey();
                    longestPair[1]=emplIds.getValue();
                }
                longestPair[2]=entry.getValue();
                maxDays=entry.getValue();
            }
        }
        return longestPair;
    }
}


