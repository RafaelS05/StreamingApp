using System.ComponentModel.DataAnnotations;

namespace streaming_app_Gestion.Series.DTO
{
    public class SeriesUpdateDto
    {
        [Required]
        [MaxLength(150)]
        public string SerieName { get; set; } = string.Empty;

        [Required]
        [MaxLength(1000)]
        public string SerieDescription { get; set; } = string.Empty;

        [Required]
        public DateOnly SerieReleaseDate { get; set; }

        // Optional on update: only replaces the cover when a new file is sent.
        public IFormFile? ImageFile { get; set; }

        [Required]
        [Range(1, int.MaxValue)]
        public int IdCategory { get; set; }

        [Required]
        [Range(1, int.MaxValue)]
        public int IdStatues { get; set; }
    }
}
