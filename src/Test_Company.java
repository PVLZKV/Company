
import java.util.Date;

public class Test_Company {
    public static void main(String[] args) {

        Date date1 = new Date();

        Company company = Company.getInstance();

        System.out.format("%-16s - %s%n", "MyCompanyName", company.getMyCompanyName());
        System.out.format("%-16s - %s%n", "MyCompanyAddress", company.getMyCompanyAddress());
        System.out.format("%-16s - %d%n", "MyCompanyAge", company.getMyCompanyAge());
        System.out.format("%-16s - %s%n", "MyCompanyOwner", company.getMyCompanyOwner());
        System.out.format("%-16s - %.2f%n", "MyCompanyCapital", company.getMyCompanyCapital());
        System.out.format("%-16s - %d%n", "EmployeesCount", company.getMyCompanyEmployeesCount());
        System.out.format("%-16s - %s%n", "MyCompanySymbol", company.getMyCompanySymbol());

        Date date2 = new Date();
        System.out.println("---------------------------------------");
        System.out.println("Class create test: " + (date2.getTime() - date1.getTime()) + " ms");
        System.out.println("---------------------------------------");


        Company.doRefresh();

        System.out.format("%-16s - %s%n", "MyCompanyName", company.getMyCompanyName());
        System.out.format("%-16s - %s%n", "MyCompanyAddress", company.getMyCompanyAddress());
        System.out.format("%-16s - %d%n", "MyCompanyAge", company.getMyCompanyAge());
        System.out.format("%-16s - %s%n", "MyCompanyOwner", company.getMyCompanyOwner());
        System.out.format("%-16s - %.2f%n", "MyCompanyCapital", company.getMyCompanyCapital());
        System.out.format("%-16s - %d%n", "EmployeesCount", company.getMyCompanyEmployeesCount());
        System.out.format("%-16s - %s%n", "MyCompanySymbol", company.getMyCompanySymbol());

        Date date3 = new Date();
        System.out.println("---------------------------------------");
        System.out.println("doRefresh invoke test: " + (date3.getTime() - date2.getTime()) + " ms");
        System.out.println("---------------------------------------");
    }
}

