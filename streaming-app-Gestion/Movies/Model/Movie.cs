using System.ComponentModel.DataAnnotations.Schema;

namespace streaming_app_Gestion.Movies.Model
{
    public class Movie
    {
        public int idMovie { get; set; }
        public string movieName { get; set; } = string.Empty;
        public string movieDescription { get; set; } = string.Empty;
        public DateOnly movieReleaseDate { get; set; }

        [ForeignKey("Image")]
        public int IdImage { get; set; }

        [ForeignKey("Category")]
        public int IdCategory { get; set; }
        public string CategoryName { get; set; } = string.Empty;

        [ForeignKey("Statues")]
        public int IdStatues { get; set; }
        public string StatusName { get; set; } = string.Empty;

    }
}
