using System.ComponentModel.DataAnnotations;

namespace streaming_app_Gestion.Categories.DTO
{
    public class CategoryUpdateDto
    {
        [Required]
        [MaxLength(55)]
        public string Name { get; set; } = string.Empty;

        [MaxLength(255)]
        public string Description { get; set; } = string.Empty;
    }
}
