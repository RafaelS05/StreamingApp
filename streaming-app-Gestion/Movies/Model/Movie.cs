using System.ComponentModel.DataAnnotations.Schema;

namespace streaming_app_Gestion.Movies.Model
{
    public class Movie
    {
        private int idMovie { get; set; }
        private string movieName { get; set; } = string.Empty;
        private string movieDescription { get; set; } = string.Empty;
        private DateOnly movieReleaseDate { get; set; }

        [ForeignKey("Image")]
        private int IdImage { get; set; }

        [ForeignKey("Category")]
        private int IdCategory { get; set; }
        private string CategoryName { get; set; } = string.Empty;

        [ForeignKey("Statues")]
        private int IdStatues { get; set; }
        private string StatusName { get; set; } = string.Empty;

    }
}
