using System.ComponentModel.DataAnnotations;

namespace streaming_app_Gestion.Movies.DTO
{
    public class MovieCreateDto
    {
        [Required]
        [MaxLength(150)]
        public string MovieName { get; set; } = string.Empty;

        [Required]
        [MaxLength(1000)]
        public string MovieDescription { get; set; } = string.Empty;

        [Required]
        public DateOnly MovieReleaseDate { get; set; }

        [Required]
        public IFormFile ImageFile { get; set; } = null!;

        [Required]
        [Range(1, int.MaxValue)]
        public int IdCategory { get; set; }

        [Required]
        [Range(1, int.MaxValue)]
        public int IdStatues { get; set; }
    }
}
