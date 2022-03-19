#include <iostream>
#include "RBAC_role_hierarchy.hpp"

using namespace std;
using namespace RBAC;

Role foo()
{
    return {"Student", Permission::register_to_course1, Session::s2};
}

int main()
{
    cout.setf(ios_base::boolalpha);

    // Set up roles
    Role Student("Student", Permission::register_to_course1, Session::s2);
    Role TA("TA", Permission::modify_course3, Session::s2);
    Role Faculty("Faculty", {Permission::delete_course1, Permission::modify_course2}, Session::s1);
    Role Lecturer("Lecturer", Permission::modify_course2, Session::s1);
    Role PCMember("PCMember", Permission::delete_course2, Session::s1);
    Faculty.add_subrole(Student);
    Lecturer.add_subrole(TA);
    PCMember.add_subrole({Faculty, Lecturer});

    // Create users
    User Bob("Bob", PCMember, Session::s2);
    User Alice("Alice", Lecturer, {Session::s1, Session::s2});
    User Oscar("Oscar", TA, {Session::s1, Session::s2});
    User Charlie("Charlie", {Student, TA}, Session::s1);

    // Test lines
    Bob.add_role(Student);
    Charlie.add_role(Faculty);

    // Show users info
    Bob.show_info(); cout << endl;
    Alice.show_info(); cout << endl;
    Oscar.show_info(); cout << endl;
    Charlie.show_info(); cout << endl;

    // Illustration lines
    cout << "Can Alice modify course 3? " << Determine(Alice, Permission::modify_course3) << "\n"
         << "Can Bob modify course 2? " << Determine(Bob, Permission::modify_course2) << "\n"
         << "Can Bob modify course 3? " << Determine(Bob, Permission::modify_course3) << "\n"
         << "Can Bob register to course 1? " << Determine(Bob, Permission::register_to_course1) << "\n"
         << "Can Alice register to course 1? " << Determine(Alice, Permission::register_to_course1) << endl;

    std::cout << "\n\n";
    User Ngoc("Ngoc", foo(), Session::s1);
    Ngoc.show_info(); cout << endl;
    return 0;
}
