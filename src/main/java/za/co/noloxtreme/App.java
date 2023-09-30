package za.co.noloxtreme;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        AIEmployeeService aiEmployeeService = new AIEmployeeService();
        aiEmployeeService.queryEmployees("Retrieve all employees that work in HR");
    }
}
