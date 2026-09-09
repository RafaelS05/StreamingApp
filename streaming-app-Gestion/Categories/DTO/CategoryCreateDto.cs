namespace streaming_app_Gestion.Categories.DTO
{
    using System.ComponentModel.DataAnnotations;
    public class CategoryCreateDto
    {
        [Required]
        [MaxLength(55)]
        public string Name { get; set; } = string.Empty;
        
        [MaxLength(255)]
        public string Description { get; set; } = string.Empty;
    }
}
