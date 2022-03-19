#ifndef RBAC_HPP_INCLUDED
#define RBAC_HPP_INCLUDED

#include <iostream>
#include <unordered_set>
#include <unordered_map>
#include <vector>
#include <utility>
#include <string>
#include <algorithm>
#include <functional>

namespace RBAC
{
enum class Session : char {s1, s2};
enum class Permission : char {delete_course1, modify_course2, modify_course3, delete_course2, register_to_course1};

class User;

bool Determine(const User&, const Permission&);

class Role
{
 private:
    class role_info
    {
     private:
        std::unordered_set<Permission> permission_list;
        std::unordered_set<Session> session_list;

     public:
        role_info(Permission permission, Session session)
        {
            permission_list.emplace(permission);
            session_list.emplace(session);
        }

        role_info(std::initializer_list<Permission> permission_list, Session session) : permission_list(permission_list)
        {
            session_list.emplace(session);
        }

        role_info(Permission permission, std::initializer_list<Session> session_list) : session_list(session_list)
        {
            permission_list.emplace(permission);
        }

        role_info(std::initializer_list<Permission> permission_list, std::initializer_list<Session> session_list)
            : permission_list(permission_list), session_list(session_list) {}

        virtual ~role_info() {}

        role_info(const role_info& other) : permission_list(other.permission_list), session_list(other.session_list) {}
        role_info(role_info&& other) noexcept : permission_list(other.permission_list), session_list(other.session_list) {};
        role_info& operator=(const role_info& other)
        {
            if(this == &other)
                return *this;

            permission_list = other.permission_list;
            session_list = other.session_list;
            return *this;
        }

        role_info& operator=(role_info&& other) noexcept
        {
            permission_list = other.permission_list;
            session_list = other.session_list;
            other.permission_list.clear();
            other.session_list.clear();
            return *this;
        }

        std::unordered_set<Permission>& get_permission_list()
        {
            return permission_list;
        }

        const std::unordered_set<Permission>& get_permission_list() const
        {
            return permission_list;
        }

        std::unordered_set<Session>& get_session_list()
        {
            return session_list;
        }

        const std::unordered_set<Session>& get_session_list() const
        {
            return session_list;
        }
    };

 protected:
    std::string name;
    role_info self_info;
    std::unordered_map<const Role*, const role_info*> role_info_list;

 public:
    Role(std::string role_name, Permission permission, Session session) : name(role_name), self_info(permission, session)
    {
        role_info_list.emplace(this, &self_info);
    }

    Role(std::string role_name, std::initializer_list<Permission> permission_list, Session session)
        : name(role_name), self_info(permission_list, session)
    {
        role_info_list.emplace(this, &self_info);
    }

    Role(std::string role_name, Permission permission, std::initializer_list<Session> session_list)
        : name(role_name), self_info(permission, session_list)
    {
        role_info_list.emplace(this, &self_info);
    }

    Role(std::string role_name, std::initializer_list<Permission> permission_list, std::initializer_list<Session> session_list)
        : name(role_name), self_info(permission_list, session_list)
    {
        role_info_list.emplace(this, &self_info);
    }

    virtual ~Role() {}

    Role(const Role& other) : name(other.name), self_info(other.self_info), role_info_list(other.role_info_list)
    {
        role_info_list.emplace(this, &self_info);
        for(auto&& i : other.role_info_list)
        {
            if(i.first == &other)
                continue;
            role_info_list.emplace(i.first, i.second);
        }
    }

    Role(Role&& other) noexcept : name(other.name), self_info(other.self_info), role_info_list(other.role_info_list)
    {
        role_info_list.erase(&other);
        role_info_list.emplace(this, &self_info);
    }

    Role& operator=(const Role& other)
    {
        if(this == &other)
            return *this;

        name = other.name;
        self_info = other.self_info;
        role_info_list.emplace(this, &self_info);
        for(auto&& i : other.role_info_list)
        {
            if(i.first == &other)
                continue;
            role_info_list.emplace(i.first, i.second);
        }

        return *this;
    }

    Role& operator=(Role&& other) noexcept
    {
        name = other.name;
        self_info = other.self_info;
        role_info_list = other.role_info_list;
        role_info_list.erase(&other);
        role_info_list.emplace(this, &self_info);

        other.name.clear();
        other.role_info_list.clear();
        return *this;
    }

    bool operator==(const Role& other) const
    {
        return this == &other;
    }

    void set_name(std::string role_name)
    {
        name = role_name;
    }

    std::string get_name() const
    {
        return name;
    }

    std::unordered_map<const Role*, role_info> get_role_info_list() const
    {
        std::unordered_map<const Role*, role_info> return_list;
        for(auto&& i : role_info_list)
        {
            return_list.emplace(i.first, *i.second);
        }
        return return_list;
    }

    void add_permission(const Permission& per)
    {
        self_info.get_permission_list().emplace(per);
    }

    void add_permission(std::initializer_list<Permission> permissions)
    {
        for(auto&& per : permissions)
            add_permission(per);
    }

    void add_session(const Session& session)
    {
        self_info.get_session_list().emplace(session);
    }

    void add_session(std::initializer_list<Session> sessions)
    {
        for(auto&& session : sessions)
            add_session(session);
    }

    void add_subrole(const Role& subrole)
    {
        for(auto&& i : subrole.role_info_list)
            role_info_list.insert_or_assign(i.first, i.second);
    }

    void add_subrole(std::initializer_list<std::reference_wrapper<const Role>> subrole_list)
    {
        for(const Role& subrole : subrole_list)
            add_subrole(subrole);
    }

    void remove_permission(const Permission& permission)
    {
        self_info.get_permission_list().erase(permission);
    }

    void remove_session(const Session& session)
    {
        self_info.get_session_list().erase(session);
    }

    bool check_permission(const Permission& per) const
    {
        for(auto&& info_pair : role_info_list)
        {
            if(info_pair.second->get_permission_list().find(per) != info_pair.second->get_permission_list().end())
                return true;
        }
        return false;
    }

    bool check_session(const Session& session) const
    {
        for(auto&& info_pair : role_info_list)
        {
            if(info_pair.second->get_session_list().find(session) != info_pair.second->get_session_list().end())
                return true;
        }
        return false;
    }

    void show_info(std::ostream& out = std::cout) const
    {
        auto per_to_string = [](const Permission& per)
        {
            return (per == Permission::delete_course1) ? "Delete course 1" :
                   (per == Permission::delete_course2) ? "Delete course 2" :
                   (per == Permission::modify_course2) ? "Modify course 2" :
                   (per == Permission::modify_course3) ? "Modify course 3" :
                                                         "Register to course 1";
        };

        auto ses_to_string = [](const Session& ses)
        {
            return (ses == Session::s1) ? "s1" : "s2";
        };

        out << "   " << name << "\n";
        for(auto&& role_info : role_info_list)
        {
            for(auto&& permission : role_info.second->get_permission_list())
                out << per_to_string(permission) << "  ";
            out << "-" << "  ";
            for(auto&& session : role_info.second->get_session_list())
                out << ses_to_string(session);
            out << "\n";
        }
    }
};

class User
{
 protected:
    std::string name;
    std::unordered_set<const Role*> role_list;
    std::unordered_set<Session> session_list;

 public:
    User(std::string user_name, const Role& role, Session session) : name(user_name)
    {
        role_list.emplace(&role);
        session_list.emplace(session);
    }

    User(std::string user_name, const Role& role, std::initializer_list<Session> session_list) : name(user_name), session_list(session_list)
    {
        role_list.emplace(&role);
    }

    User(std::string user_name, std::initializer_list<std::reference_wrapper<const Role>> role_list, Session session) : name(user_name)
    {
        for(const Role& role : role_list)
        {
            this->role_list.emplace(&role);
        }
        session_list.emplace(session);
    }

    User(std::string user_name, std::initializer_list<std::reference_wrapper<const Role>> role_list, std::initializer_list<Session> session_list)
        : name(user_name), session_list(session_list)
    {
        for(const Role& role : role_list)
        {
            this->role_list.emplace(&role);
        }
    }

    virtual ~User() {}

    User(const User& other) : name(other.name), role_list(other.role_list), session_list(other.session_list) {}
    User(User&& other) noexcept : name(other.name), role_list(other.role_list), session_list(other.session_list) {}
    User& operator=(const User& other)
    {
        if(this == &other)
            return *this;

        name = other.name;
        role_list = other.role_list;
        session_list = other.session_list;
    }

    User& operator=(User&& other) noexcept
    {
        name = other.name;
        role_list = other.role_list;
        session_list = other.session_list;
        other.name.clear();
        other.role_list.clear();
        other.session_list.clear();
        return *this;
    }

    void set_name(const std::string& user_name)
    {
        name = user_name;
    }

    std::string get_name() const
    {
        return name;
    }

    void add_role(const Role& role)
    {
        std::unordered_set<const Role*> inserted_identities;
        bool special_case = false;
        for(auto&& info_pair : role.get_role_info_list())
        {
            inserted_identities.emplace(info_pair.first);
        }

        for(const Role* role_ptr : role_list)
        {
            std::unordered_set<const Role*> owned_identities;
            for(auto&& info_pair : role_ptr->get_role_info_list())
            {
                owned_identities.emplace(info_pair.first);
            }

            bool skip = false;
            if(owned_identities.size() >= inserted_identities.size())
            {
                for(auto&& id : inserted_identities)
                {
                    if(owned_identities.find(id) != owned_identities.end())
                    {
                        skip = true;
                        special_case = true;
                        break;
                    }
                }
            }
            else
            {
                bool update_role = false;
                for(auto&& id : owned_identities)
                {
                    if(inserted_identities.find(id) != inserted_identities.end())
                    {
                        update_role = true;
                        break;
                    }
                }

                if(update_role)
                    role_list.erase(role_ptr);
            }

            if(skip)
                break;
        }

        if(!special_case)
            role_list.emplace(&role);
    }

    void add_role(std::initializer_list<std::reference_wrapper<const Role>> role_list)
    {
        for(const Role& role : role_list)
            add_role(role);
    }

    void add_session(const Session& session)
    {
        session_list.emplace(session);
    }

    void add_session(std::initializer_list<Session> sessions)
    {
        for(auto&& session : sessions)
            add_session(session);
    }

    void remove_role(const Role& role)
    {
        role_list.erase(&role);
    }

    void remove_session(const Session& session)
    {
        session_list.erase(session);
    }

    bool check_role(const Role& role) const
    {
        return (role_list.find(&role) != role_list.end()) ? true : false;
    }

    bool check_session(Session session) const
    {
        return (session_list.find(session) != session_list.end()) ? true : false;
    }

    std::vector<Role> get_role_list() const
    {
        std::vector<Role> return_list;
        return_list.reserve(role_list.size());
        for(auto&& role_ptr : role_list)
            return_list.emplace_back(*role_ptr);
        return return_list;
    }

    std::unordered_set<Session> get_session_list() const
    {
        return session_list;
    }

    void show_info(std::ostream& out = std::cout) const
    {
        auto ses_to_string = [](const Session& ses)
        {
            return (ses == Session::s1) ? "s1" : "s2";
        };

        out << "Name: " << name << "\n";
        out << "Session: ";
        for(auto&& session : session_list)
            out << ses_to_string(session) << "  ";
        out << "\n";
        out << "Role:" << "\n";
        for(auto&& role_ptr : role_list)
            role_ptr->show_info();
    }
};

bool Determine(const User& user, const Permission& per)
{
    bool flag = false;
    for(auto&& role : user.get_role_list())
    {
        bool per_found = false;
        std::unordered_set<Session> session_list;
        for(auto&& info_pair : role.get_role_info_list())
        {
            if(info_pair.second.get_permission_list().find(per) != info_pair.second.get_permission_list().end())
            {
                session_list = info_pair.second.get_session_list();
                per_found = true;
            }
        }

        if(!per_found)
            continue;

        for(auto&& session : session_list)
        {
            if(user.check_session(session) == true)
            {
                flag = true;
                break;
            }
        }

        if(flag == true)
            break;
    }

    return flag;
}
}

#endif // RBAC_HPP_INCLUDED
