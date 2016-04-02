namespace Notifications.DbContext
{
    internal class ApplicationDatabaseContext : System.Data.Entity.DbContext
    {
        public ApplicationDatabaseContext() : base("DefaultConnection")
        {
        }
    }
}