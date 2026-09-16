using System.ComponentModel.DataAnnotations;

namespace streaming_app_Gestion.Reviews.DTO
{
    public class ReviewUpdateDto
    {
        [Required]
        [Range(1, 5)]
        public int Rating { get; set; }

        [Required]
        [MaxLength(1000)]
        public string Comment { get; set; } = string.Empty;
    }
}
