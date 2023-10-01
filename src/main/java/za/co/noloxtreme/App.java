package za.co.noloxtreme;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        AIEmployeeService aiEmployeeService = new AIEmployeeService();
        String result = aiEmployeeService.queryEmployees("Retrieve all employees that work in Sales or HR");
        System.out.println(result);
    }
}
