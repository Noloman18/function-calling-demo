package za.co.noloxtreme;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        AIEmployeeService aiEmployeeService = new AIEmployeeService();
        //String result = aiEmployeeService.queryEmployees("Retrieve all employees that are between 30 and 35 years old");
        String result = aiEmployeeService.queryEmployees("Retrieve all employees that are less than 30 years old");
        System.out.println(result);
    }
}
