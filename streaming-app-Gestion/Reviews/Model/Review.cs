using System.ComponentModel.DataAnnotations.Schema;

namespace streaming_app_Gestion.Reviews.Model
{
    public class Review
    {
        public int IdReview { get; set; }
        public int Rating { get; set; }
        public string Comment { get; set; } = string.Empty;

        // A review targets a movie OR a serie — hence both are nullable.
        [ForeignKey("Movie")]
        public int? IdMovie { get; set; }

        [ForeignKey("Serie")]
        public int? IdSerie { get; set; }

        [ForeignKey("User")]
        public Guid IdUser { get; set; }

        public DateTime ReviewDate { get; set; } = DateTime.UtcNow;
    }
}
