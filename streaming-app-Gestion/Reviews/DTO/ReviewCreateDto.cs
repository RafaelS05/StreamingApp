using System.ComponentModel.DataAnnotations;

namespace streaming_app_Gestion.Reviews.DTO
{
    public class ReviewCreateDto
    {
        [Required]
        [Range(1, 5)]
        public int Rating { get; set; }

        [Required]
        [MaxLength(1000)]
        public string Comment { get; set; } = string.Empty;

        // A review targets a movie OR a serie — supply one of the two.
        [Range(1, int.MaxValue)]
        public int? IdMovie { get; set; }

        [Range(1, int.MaxValue)]
        public int? IdSerie { get; set; }

        [Required]
        public Guid IdUser { get; set; }
    }
}
