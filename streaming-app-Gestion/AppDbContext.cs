using Microsoft.EntityFrameworkCore;
using streaming_app_Gestion.Categories.Model;
using streaming_app_Gestion.ErrorHandler.Model;
using streaming_app_Gestion.Image.Model;
using streaming_app_Gestion.Movies.Model;
using streaming_app_Gestion.Reviews.Model;
using streaming_app_Gestion.Series.Model;

namespace streaming_app_Gestion
{
    public class AppDbContext : DbContext
    {

        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
        {
        }

        public DbSet<Category> Categories { get; set; }
        public DbSet<Error> Errors { get; set; }
        //public DbSet<Image> Images { get; set; }
        public DbSet<Movie> Movies { get; set; }
        public DbSet<Review> Reviews { get; set; }
        public DbSet<Serie> Series { get; set; }



        //public DbSet<Subscription> Subscriptions { get; set; }

    }
}
