using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using streaming_app_Gestion.Categories.Service;
using streaming_app_Gestion.Categories.DTO;

namespace streaming_app_Gestion.Categories.Controllers
{
    [ApiController]
    [Route("api/v1/[controller]")]
    public class CategoriesController : Controller
    {
        private readonly ICategoryService _categoriesService;

        public CategoriesController(ICategoryService categoriesService)
        {
            _categoriesService = categoriesService;
        }

        [HttpGet]
        // GET: CategoryController
        //Asigno el valor de task ya que se debe ejecutar una "tarea"
        //Con el ActionResult<List para generar una lista en base a
        //Un objeto deaseado en este caso el CategoryResponse
        public async Task<ActionResult<List<CategoryResponseDto>>> CategoriesList(CancellationToken ct)
        {
            //Creo la variable sobre la cual llamar el servicio que tiene capa de servicio para obtener lo deaseado, esto aplica
            //Sobre todos los metodos que tiene la interfaz
            var category = await _categoriesService.GetAllCategoriesAsync(ct);

            //Valida que las categories no sean null o si no encuentra
            //De esto devuelve el OK con el contenido de categories el cual llama el metodo de GetAll
            return category is null ? NotFound() : Ok(category);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<CategoryResponseDto>> CategoriesID(int id, CancellationToken ct)
        {
            var category = await _categoriesService.GetCategoryByIdAsync(id, ct);

            return category is null ? NotFound() : Ok(category);
        }

        // POST: CategoryController/Create
        [HttpPost]
        public async Task<ActionResult<CategoryResponseDto>> CreateCategory([FromBody] CategoryCreateDto categoryCreateDto //FromBody indica que el objeto se va a recibir desde el cuerpo de la solicitud HTTP
                                                                                                                           //y se va a deserializar automáticamente en un objeto de tipo CategoryCreateDto
            , CancellationToken ct)
        {
            //Dentro del try catch se llama al metodo de la capa de servicio para crear la categoria, si todo sale bien devuelve el CreatedAtAction con http(201) con el id de la categoria creada
            try
            {
                var category = await _categoriesService.CreateCategoryAsync(categoryCreateDto, ct);

                return CreatedAtAction(nameof(CategoriesID), new { id = category.IdCategory }, category);
            }
            //Si hay un error de argumento, devuelve un BadRequest con el mensaje de error
            catch (ArgumentException ex)
            {
                return BadRequest(ex.Message);
            }
            //Si hay un error general, devuelve un StatusCode con el mensaje de error
            catch (Exception)
            {
                return StatusCode(StatusCodes.Status500InternalServerError, new { message = "Error creating new category" });
            }
        }

        // POST: CategoryController/Update/
        [HttpPut("{id}")]
        public async Task<ActionResult<CategoryResponseDto>> UpdateCategory(int id, [FromBody] CategoryUpdateDto categoryUpdateDto, CancellationToken ct)
        {
            try
            {
                var category = await _categoriesService.UpdateCategoryAsync(id, categoryUpdateDto, ct);

                return category is null ? NotFound() : Ok(category);
            }
            catch (ArgumentException ex)
            {
                return BadRequest(ex.Message);
            }
            catch (Exception)
            {
                return StatusCode(StatusCodes.Status500InternalServerError, new { message = "Error creating new category" });
            }
        }


        // POST: CategoryController/Delete/
        [HttpPost]
        //Por implementar ya que hace borrado total, lo que se puede hacer es un borrado logico, es decir cambiar el estado de la categoria a inactivo y no eliminarla de la base de datos
        public async Task<ActionResult<CategoryResponseDto>> Delete(int id, CancellationToken ct)
        {
            try
            {
                var category = await _categoriesService.DeleteCategoryAsync(id, ct);    

                return Ok(category);
            }
            catch (ArgumentException ex)
            {
                return BadRequest(ex.Message);
            }
            catch (Exception)
            {
                return StatusCode(StatusCodes.Status500InternalServerError, new { message = "Error creating new category" });
            }
        }

        //[ValidateAntiForgeryToken] Este atributo se utiliza para proteger contra ataques de falsificación de solicitudes entre sitios (CSRF).
        //Se asegura de que la solicitud provenga de una fuente confiable y no de un atacante externo.
        //Al incluir este atributo, se requiere que la solicitud incluya un token de verificación válido, lo que ayuda a prevenir ataques maliciosos.
        //SOLO SE USA EN MVC, NO EN API REST
    }
}
