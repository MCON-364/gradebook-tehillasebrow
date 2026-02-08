package edu.course.gradebook;

import java.util.*;

public class Gradebook {

    private final Map<String, List<Integer>> gradesByStudent = new HashMap<>();
    private final Deque<UndoAction> undoStack = new ArrayDeque<>();
    private final LinkedList<String> activityLog = new LinkedList<>();

    public Optional<List<Integer>> findStudentGrades(String name) {
        return Optional.ofNullable(gradesByStudent.get(name));
    }

    public boolean addStudent(String name) {

        if (gradesByStudent.containsKey(name)) {
            return false;
        } else {
            gradesByStudent.put(name, new ArrayList<Integer>());
            activityLog.add("added Student" + name);

        }
        return true;
    }

    public boolean addGrade(String name, int grade) {
        var gradesOpt = findStudentGrades(name);
        if (gradesOpt.isEmpty())
            return false;
        else
            gradesByStudent.get(name).add(grade);
        activityLog.push("added " + name + "'s grade: " + grade);

        UndoAction undoAction = (gradebook) -> {
            gradebook.gradesByStudent.get(name).remove(Integer.valueOf(grade));
        };
        undoStack.push(undoAction);
        return true;
    }

    public boolean removeStudent(String name) {
        var gradesOpt = findStudentGrades(name);
        if (gradesOpt.isEmpty())
            return false;
        List<Integer> oldGrades = new ArrayList<>(gradesOpt.get());
        gradesByStudent.remove(name);
        UndoAction undoAction = (gradebook) -> {
           gradebook.gradesByStudent.put(name, oldGrades);
        };
        undoStack.push(undoAction);
        activityLog.add("Removed " + name + " from the Gradebook.");

        return true;
    }

    ;

    public Optional<Double> averageFor(String name) {
        var grades = findStudentGrades(name);
        if (grades.isEmpty())
            return Optional.empty();
        var gradesList = grades.get();
        if (gradesList.isEmpty())
            return Optional.empty();
        double sum = 0;

        for (int grade : gradesList) {
            sum += grade;
        }
        return Optional.of(sum / gradesList.size());
    }

    public Optional<String> letterGradeFor(String name) {
        var avg=averageFor(name);
        if (avg.isEmpty())
            return Optional.empty();
double avrg=avg.get();
int firstNum= (int) avrg/10;
     String letter=   switch(firstNum){
           case 10, 9-> "A";
           case  8-> "B";
           case 7 -> "C";
           case 6-> "D";
         default -> "F";
     };
        return Optional.of(letter);
    }

    public Optional<Double> classAverage() {
       double totalSum=0;
       int ctr=0;
        for(List<Integer> value: gradesByStudent.values()){
            for(Integer grades:value){
                totalSum+=grades;
                ctr++;
            }
       }
        if (totalSum == 0)return Optional.empty();
        return Optional.of(totalSum/ctr);
    }

    public boolean undo() {
      if(undoStack.isEmpty())
          return false;
        UndoAction task= undoStack.pop();
        task.undo(this);//this i used chatgpt since i didnt know what was wrong with just doing task.undo(); i didnt know you needed a 'this'

      activityLog.add("undo previous task");
return true;
    }

    public List<String> recentLog(int maxItems) {
        Iterator<String> iterator=activityLog.descendingIterator();
        List<String> list= new ArrayList<>();
        int ctr=0;
        while (iterator.hasNext() && ctr < maxItems){
        list.add(iterator.next());
        ctr++;
        }
        return list;
    }
}
